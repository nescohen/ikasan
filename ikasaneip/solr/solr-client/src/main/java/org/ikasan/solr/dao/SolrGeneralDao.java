package org.ikasan.solr.dao;

import java.util.List;
import java.util.Set;

/**
 * Created by Ikasan Development Team on 04/08/2017.
 */
public interface SolrGeneralDao<RESULTS>
{
    /**
     * Perform general search against ikasan solr index.
     *
     * @param moduleName
     * @param flowNames
     * @param searchString
     * @param startTime
     * @param endTime
     * @param resultSize
     * @return RESULTS
     */
    public RESULTS search(Set<String> moduleName, Set<String> flowNames, String searchString, long startTime, long endTime, int resultSize);


    /**
     * Perform general search against ikasan solr index.
     *
     * @param moduleName
     * @param flowNames
     * @param searchString
     * @param startTime
     * @param endTime
     * @param resultSize
     * @param entityTypes
     * @return RESULTS
     */
    public RESULTS search(Set<String> moduleName, Set<String> flowNames, String searchString, long startTime, long endTime, int resultSize, List<String> entityTypes);

    /**
     * Perform general search against ikasan solr index.
     *
     * @param moduleName
     * @param flowNames
     * @param componentNames
     * @param eventId
     * @param searchString
     * @param startTime
     * @param endTime
     * @param offset
     * @param resultSize
     * @param entityTypes
     * @return
     */
    public RESULTS search(Set<String> moduleName, Set<String> flowNames, Set<String> componentNames, String eventId, String searchString, long startTime, long endTime, int offset, int resultSize, List<String> entityTypes);



    /**
     * Perform general search against ikasan solr index.
     *
     * @param searchString
     * @param startTime
     * @param endTime
     * @param resultSize
     * @param entityTypes
     * @return RESULTS
     */
    public RESULTS search(String searchString, long startTime, long endTime, int resultSize, List<String> entityTypes);

    /**
     * Perform general search against ikasan solr index.
     *
     * @param searchString
     * @param startTime
     * @param endTime
     * @param offset
     * @param resultSize
     * @param entityTypes
     * @return RESULTS
     */
    public RESULTS search(String searchString, long startTime, long endTime, int offset, int resultSize, List<String> entityTypes);

    /**
     * Set the solr username
     *
     * @param solrUsername
     */
    public void setSolrUsername(String solrUsername);


    /**
     * Set the solr password
     *
     * @param solrPassword
     */
    public void setSolrPassword(String solrPassword);

    /**
     * Method to remove expired records from the solr index.
     */
    public void removeExpired();

    /**
     * Method to remove records from the solr index by type and id.
     *
     * @param type
     */
    public void removeById(String type, String id);
}
