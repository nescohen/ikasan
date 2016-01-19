/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.wiretap.dao;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.wiretap.model.ArrayListPagedSearchResult;
import org.ikasan.wiretap.model.WiretapFlowEvent;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

/**
 * Hibernate implementation of the <code>WiretapDao</code>
 *
 * @author Ikasan Development Team
 *
 */
public class HibernateWiretapDao extends HibernateDaoSupport implements WiretapDao
{

	private static final String EXPIRY = "expiry";
	private static final String EVENT_ID = "eventId";
	private static final String BATCH_SIZE = "batchSize";
	private static final String DELETED_IDS = "deletedIds";

    /** Query used for housekeeping expired wiretap events */
    private static final String HOUSEKEEP_QUERY = "delete WiretapFlowEvent w where w.expiry <= :" + EXPIRY;

    /** Query for finding all wiretap events with the same payloadId */
    private static final String WIRETAP_IDS_FOR_GROUPED_EVENT_ID = "select w.id from WiretapFlowEvent w where w.eventId = :" + EVENT_ID;

    /** Batch select statement */
    public static final String SELECT_EXPIRED = " (select top " + BATCH_SIZE + " Id from IkasanWiretap where Expiry <= " + EXPIRY + ")";

    /** Delete statement */
    private static final String BATCHED_HOUSEKEEP_QUERY = "delete from IkasanWiretap where Id in :" + DELETED_IDS;

    private static final String HOUSEKEEPABLES_EXIST = "SELECT CAST(COUNT(1) AS BIT) AS HousekeepableExists FROM IkasanWiretap" +
    		" WHERE EXISTS (select * from IkasanWiretap where Expiry <= " + EXPIRY + ")";


    /** Use batch housekeeping mode? */
    private boolean batchHousekeepDelete = false;

    /** Batch size used when in batching housekeep */
	private Integer housekeepingBatchSize = 1000;

	/** Batch size used when in a single transaction */
	private Integer transactionBatchSize = 5000;

    /**
     * Constructor
     */
    public HibernateWiretapDao() {
		super();
	}

    /**
     * Constructor
     *
     * @param batchHousekeepDelete - pass true if you want to use batch deleting
     * @param housekeepingBatchSize - batch size, only respected if set to use batching
     */
    public HibernateWiretapDao(boolean batchHousekeepDelete,
			Integer housekeepingBatchSize) {
		this();
		this.batchHousekeepDelete = batchHousekeepDelete;
		this.housekeepingBatchSize = housekeepingBatchSize;
	}

	/**
     * Save the wiretapFlowEvent
     *
     * @see
     * org.ikasan.framework.event.wiretap.dao.WiretapDao#save(
     * org.ikasan.framework.event.wiretap.model.WiretapFlowEvent)
     */
    public void save(WiretapEvent wiretapEvent)
    {
        getHibernateTemplate().save(wiretapEvent);
    }

    /**
     * Find the Wiretap by its Id
     *
     * @see
     * org.ikasan.framework.event.wiretap.dao.WiretapDao#findById(java.lang.
     * Long)
     */
    @SuppressWarnings("unchecked")
    public WiretapEvent findById(final Long identifier)
    {
    	return (WiretapFlowEvent)this.getHibernateTemplate().execute(new HibernateCallback()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
            	WiretapFlowEvent wiretapEvent = (WiretapFlowEvent) getHibernateTemplate().get(WiretapFlowEvent.class, identifier);

                Query query = session.createQuery(WIRETAP_IDS_FOR_GROUPED_EVENT_ID);
                query.setParameter(EVENT_ID, wiretapEvent.getEventId());


                List<Long> relatedIds = (List<Long>)query.list();

                Collections.sort(relatedIds);
                int thisWiretapsIndex = relatedIds.indexOf(wiretapEvent.getIdentifier());
                Long nextEvent = null;
                Long previousEvent = null;
                if (thisWiretapsIndex > 0)
                {
                    previousEvent = relatedIds.get(thisWiretapsIndex - 1);
                }
                if (thisWiretapsIndex < relatedIds.size() - 1)
                {
                    nextEvent = relatedIds.get(thisWiretapsIndex + 1);
                }
                wiretapEvent.setNextByEventId(nextEvent);
                wiretapEvent.setPreviousByEventId(previousEvent);
                return wiretapEvent;
            }
        });
    }

    /**
     * Perform a paged search for <code>WiretapFlowEvent</code>s
     *
     * @param pageNo - The page number to retrieve
     * @param pageSize - The size of the page
     * @param orderBy - order by field
     * @param orderAscending - ascending flag
     * @param moduleNames - The list of module names
     * @param moduleFlow - The name of Flow internal to the Module
     * @param componentName - The component name
     * @param eventId - The event id
     * @param payloadId - The payload id
     * @param fromDate - The from date
     * @param untilDate - The to date
     * @param payloadContent - The payload content
     *
     * @return PagedSearchResult
     */
    @SuppressWarnings("unchecked")
    public PagedSearchResult<WiretapEvent> findWiretapEvents(final int pageNo, final int pageSize, final String orderBy, final boolean orderAscending,
            final Set<String> moduleNames, final String moduleFlow, final String componentName, final String eventId, final String payloadId, final Date fromDate, final Date untilDate,
            final String payloadContent)
    {
        return (PagedSearchResult) getHibernateTemplate().execute(new HibernateCallback<Object>()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
                Criteria dataCriteria = getCriteria(session);
                dataCriteria.setMaxResults(pageSize);
                int firstResult = pageNo * pageSize;
                dataCriteria.setFirstResult(firstResult);
                if (orderBy != null)
                {
                    if (orderAscending)
                    {
                        dataCriteria.addOrder(Order.asc(orderBy));
                    }
                    else
                    {
                        dataCriteria.addOrder(Order.desc(orderBy));
                    }
                }
                List<WiretapEvent> wiretapResults = dataCriteria.list();

                Criteria metaDataCriteria = getCriteria(session);
                metaDataCriteria.setProjection(Projections.rowCount());
                Long rowCount = new Long(0);
                List<Long> rowCountList = metaDataCriteria.list();
                if (!rowCountList.isEmpty())
                {
                    rowCount = rowCountList.get(0);
                }

                return new ArrayListPagedSearchResult<WiretapEvent>(wiretapResults, firstResult, rowCount);
            }

            /**
             * Create a criteria instance for each invocation of data or metadata queries.
             * @param session
             * @return
             */
            private Criteria getCriteria(Session session)
            {
                Criteria criteria = session.createCriteria(WiretapEvent.class);

                if (restrictionExists(moduleNames))
                {
                    criteria.add(Restrictions.in("moduleName", moduleNames));
                }
                if (restrictionExists(moduleFlow))
                {
                    criteria.add(Restrictions.eq("flowName", moduleFlow));
                }
                if (restrictionExists(componentName))
                {
                    criteria.add(Restrictions.eq("componentName", componentName));
                }
                if (restrictionExists(eventId))
                {
                    criteria.add(Restrictions.eq("eventId", eventId));
                }
                if (restrictionExists(payloadContent))
                {
                    criteria.add(Restrictions.like("event", payloadContent, MatchMode.ANYWHERE));
                }
                if (restrictionExists(fromDate))
                {
                    criteria.add(Restrictions.gt("timestamp", fromDate.getTime()));
                }
                if (restrictionExists(untilDate))
                {
                    criteria.add(Restrictions.lt("timestamp", untilDate.getTime()));
                }

                return criteria;
            }
        });
    }

    /* (non-Javadoc)
	 * @see org.ikasan.wiretap.dao.WiretapDao#findWiretapEvents(int, int, java.lang.String, boolean, java.util.Set, java.util.Set, java.util.Set, java.lang.String, java.lang.String, java.util.Date, java.util.Date, java.lang.String)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PagedSearchResult<WiretapEvent> findWiretapEvents(final int pageNo, final int pageSize, final String orderBy, final boolean orderAscending,
			final Set<String> moduleNames, final Set<String> moduleFlows, final Set<String> componentNames, final String eventId, final String payloadId,
			final Date fromDate, final Date untilDate, final String payloadContent)
	{
	 	return (PagedSearchResult) getHibernateTemplate().execute(new HibernateCallback<Object>()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
                Criteria dataCriteria = getCriteria(session);
                dataCriteria.setMaxResults(pageSize);
                int firstResult = pageNo * pageSize;
                dataCriteria.setFirstResult(firstResult);
                if (orderBy != null)
                {
                    if (orderAscending)
                    {
                        dataCriteria.addOrder(Order.asc(orderBy));
                    }
                    else
                    {
                        dataCriteria.addOrder(Order.desc(orderBy));
                    }
                }
                List<WiretapEvent> wiretapResults = dataCriteria.list();

                Criteria metaDataCriteria = getCriteria(session);
                metaDataCriteria.setProjection(Projections.rowCount());
                Long rowCount = new Long(0);
                List<Long> rowCountList = metaDataCriteria.list();
                if (!rowCountList.isEmpty())
                {
                    rowCount = rowCountList.get(0);
                }

                return new ArrayListPagedSearchResult<WiretapEvent>(wiretapResults, firstResult, rowCount);
            }

            /**
             * Create a criteria instance for each invocation of data or metadata queries.
             * @param session
             * @return
             */
            private Criteria getCriteria(Session session)
            {
                Criteria criteria = session.createCriteria(WiretapEvent.class);

                if (restrictionExists(moduleNames))
                {
                    criteria.add(Restrictions.in("moduleName", moduleNames));
                }
                if (restrictionExists(moduleFlows))
                {
                    criteria.add(Restrictions.in("flowName", moduleFlows));
                }
                if (restrictionExists(componentNames))
                {
                    criteria.add(Restrictions.in("componentName", componentNames));
                }
                if (restrictionExists(eventId))
                {
                    criteria.add(Restrictions.eq("eventId", eventId));
                }
                if (restrictionExists(payloadContent))
                {
                    criteria.add(Restrictions.like("event", payloadContent, MatchMode.ANYWHERE));
                }
                if (restrictionExists(fromDate))
                {
                    criteria.add(Restrictions.gt("timestamp", fromDate.getTime()));
                }
                if (restrictionExists(untilDate))
                {
                    criteria.add(Restrictions.lt("timestamp", untilDate.getTime()));
                }

                return criteria;
            }
        });
	}

    /**
     * Check to see if the restriction exists
     *
     * @param restrictionValue - The value to check
     * @return - true if the restriction exists for that value, else false
     */
    static final boolean restrictionExists(Object restrictionValue)
    {
        // If the value passed in is not null and not an empty string then it
        // can have a restriction applied
        if (restrictionValue != null && !"".equals(restrictionValue))
        {
            return true;
        }
        return false;
    }

    /**
     * Delete all of the expired wiretaps
     */
    public void deleteAllExpired()
    {
    	if (!batchHousekeepDelete)
    	{
    		getHibernateTemplate().execute(new HibernateCallback<Object>()
	        {
	            public Object doInHibernate(Session session) throws HibernateException
	            {

	                Query query = session.createQuery(HOUSEKEEP_QUERY);
	                query.setParameter(EXPIRY, System.currentTimeMillis());
	            	query.executeUpdate();
	                return null;
	            }
	        });
    	}
    	else
    	{
			batchHousekeepDelete();
		}
    }

	/**
	 * Housekeep using batching.
	 *
	 *  Loops, checking for housekeepable items. If they exist, it identifies a batch
	 *  and attempts to delete that batch
	 */
	private void batchHousekeepDelete() {
		logger.info("Wiretap batched housekeeper called");

		int numberDeleted = 0;

		while(housekeepablesExist() && numberDeleted < this.transactionBatchSize)
		{

			numberDeleted += this.housekeepingBatchSize;

			getHibernateTemplate().execute(new HibernateCallback<Object>()
	        {
	            public Object doInHibernate(Session session) throws HibernateException
	            {
					String selectExpired = SELECT_EXPIRED.replace(BATCH_SIZE, Integer.toString(housekeepingBatchSize))
												.replace(EXPIRY, Long.toString(System.currentTimeMillis()));
					SQLQuery selectSQLQuery = session.createSQLQuery(selectExpired);
					List deletedIds = selectSQLQuery.list();

					Query query = session.createSQLQuery(BATCHED_HOUSEKEEP_QUERY).setParameterList(DELETED_IDS, deletedIds);
					query.executeUpdate();

					return null;
	            }
	        });
		}

	}


	/**
	 * Checks if there are housekeepable items in existance, ie expired WiretapFlowEvents
	 *
	 * @return true if there is at least 1 expired WiretapFlowEvent
	 */
	public boolean housekeepablesExist()
	{
		return (Boolean) getHibernateTemplate().execute(new HibernateCallback<Object>()
        {
            public Object doInHibernate(Session session) throws HibernateException
            {
	        	 Criteria criteria = session.createCriteria(WiretapEvent.class);
	             criteria.add(Restrictions.lt("expiry", System.currentTimeMillis()));
	             criteria.setProjection(Projections.rowCount());
	             Long rowCount = new Long(0);
	             List<Long> rowCountList = criteria.list();

	             if (!rowCountList.isEmpty())
	             {
	                 rowCount = rowCountList.get(0);
	             }
	             logger.info(rowCount+", Wiretap housekeepables exist");
	             return new Boolean(rowCount>0);
            }
        });
	}



    public boolean isBatchHousekeepDelete()
    {
        return batchHousekeepDelete;
    }

    public void setBatchHousekeepDelete(boolean batchHousekeepDelete)
    {
        this.batchHousekeepDelete = batchHousekeepDelete;
    }

    public Integer getHousekeepingBatchSize()
    {
        return housekeepingBatchSize;
    }

    public void setHousekeepingBatchSize(Integer housekeepingBatchSize)
    {
        this.housekeepingBatchSize = housekeepingBatchSize;
    }

	/**
	 * @return the transactionBatchSize
	 */
	public Integer getTransactionBatchSize()
	{
		return transactionBatchSize;
	}

	/**
	 * @param transactionBatchSize the transactionBatchSize to set
	 */
	public void setTransactionBatchSize(Integer transactionBatchSize)
	{
		this.transactionBatchSize = transactionBatchSize;
	}
}
