package org.ikasan.dashboard.ui.search.component;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.server.VaadinService;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.ikasan.dashboard.security.SecurityUtils;
import org.ikasan.dashboard.ui.general.component.NotificationHelper;
import org.ikasan.dashboard.ui.search.component.filter.SearchFilter;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.spec.solr.SolrGeneralService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SolrSearchFilteringGrid extends Grid<IkasanSolrDocument>
{
    private Logger logger = LoggerFactory.getLogger(SolrSearchFilteringGrid.class);

    private SolrGeneralService<IkasanSolrDocument, IkasanSolrDocumentSearchResults> solrSearchService;

    private DataProvider<IkasanSolrDocument,SearchFilter> dataProvider;
    private ConfigurableFilterDataProvider<IkasanSolrDocument,Void, SearchFilter> filteredDataProvider;

    private SearchFilter searchFilter;

    private long resultSize = 0;
    private long queryTime = 0;

    private Label resultsLabel;

    /**
     * Constructors
     */
    public SolrSearchFilteringGrid(SolrGeneralService<IkasanSolrDocument, IkasanSolrDocumentSearchResults> solrSearchService,
                                   SearchFilter searchFilter, Label resultsLabel)
    {
        this.solrSearchService = solrSearchService;
        if(this.solrSearchService ==  null)
        {
            throw new IllegalArgumentException("solrSearchService cannot be null!");
        }
        this.searchFilter = searchFilter;
        if(this.searchFilter ==  null)
        {
            throw new IllegalArgumentException("SearchFilter cannot be null!");
        }
        this.resultsLabel = resultsLabel;
        if(this.resultsLabel ==  null)
        {
            throw new IllegalArgumentException("resultsLabel cannot be null!");
        }
    }

    /**
     * Add filtering to a column.
     *
     * @param hr
     * @param setFilter
     * @param columnKey
     */
    public void addGridFiltering(HeaderRow hr, Consumer<String> setFilter, String columnKey)
    {
        TextField textField = new TextField();
        textField.setWidthFull();

        textField.addValueChangeListener(ev->{

            setFilter.accept(ev.getValue());

            filteredDataProvider.refreshAll();
        });

        hr.getCell(getColumnByKey(columnKey)).setComponent(textField);
    }

    public void init(long startTime, long endTime, String searchTerm, List<String> types, boolean negateQuery) {
        this.init(startTime, endTime, searchTerm, types, negateQuery, null);
    }

    public void init(long startTime, long endTime, String searchTerm, List<String> types, boolean negateQuery, SearchFilter searchFilter)
    {
        if(searchFilter != null) {
            this.searchFilter = searchFilter;
        }

        IkasanAuthentication authentication = (IkasanAuthentication) SecurityContextHolder.getContext().getAuthentication();

        dataProvider = DataProvider.fromFilteringCallbacks(query ->
        {
            Optional<SearchFilter> filter = query.getFilter();

            // The index of the first item to load
            int offset = query.getOffset();

            // The number of items to load
            int limit = query.getLimit();

            IkasanSolrDocumentSearchResults results;

            if(filter.isPresent())
            {
                if(query.getSortOrders().size() > 0)
                {
                    results = this.getResults(authentication, filter.get(), startTime, endTime
                        , searchTerm, offset, limit, types, negateQuery, query.getSortOrders().get(0).getSorted()
                        , query.getSortOrders().get(0).getDirection().name());
                }
                else
                {
                    results = this.getResults(authentication, filter.get(), startTime, endTime
                        , searchTerm, offset, limit, types, negateQuery, null, null);
                }
            }
            else
            {
                if(authentication.hasGrantedAuthority("ALL"))
                {
                    if(query.getSortOrders().size() > 0)
                    {
                        results = this.solrSearchService.search(searchTerm,
                            startTime, endTime, offset, limit, types, negateQuery, query.getSortOrders().get(0).getSorted()
                            , query.getSortOrders().get(0).getDirection().name());
                    }
                    else
                    {
                        results = this.solrSearchService.search(searchTerm,
                            startTime, endTime, offset, limit, types, negateQuery, null, null);
                    }
                }
                else {
                    Set<String> moduleNames = SecurityUtils.getAccessibleModules(authentication);
                    if (moduleNames.isEmpty()) {
                        moduleNames.add("--");
                    }
                    if (query.getSortOrders().size() > 0) {
                        results = this.solrSearchService.search(moduleNames, searchTerm,
                            startTime, endTime, offset, limit, types, negateQuery, query.getSortOrders().get(0).getSorted()
                            , query.getSortOrders().get(0).getDirection().name());
                    }
                    else
                    {
                        results = this.solrSearchService.search(moduleNames, searchTerm,
                            startTime, endTime, offset, limit, types, negateQuery,null, null);
                    }
                }
            }

            return results.getResultList().stream();
        }, query ->
        {
            Optional<SearchFilter> filter = query.getFilter();

            IkasanSolrDocumentSearchResults results;

            if(filter.isPresent())
            {
                results = this.getResults(authentication, filter.get(), startTime, endTime
                    , searchTerm, 0, 0, types, negateQuery, null, null);
            }
            else
            {
                if(authentication.hasGrantedAuthority("ALL"))
                {
                    results = this.solrSearchService.search(searchTerm,
                        startTime, endTime, 0, 0, types, negateQuery, null, null);
                }
                else
                {
                    Set<String> moduleNames = SecurityUtils.getAccessibleModules(authentication);
                    if(moduleNames.isEmpty()){
                        moduleNames.add("--");
                    }

                    results = this.solrSearchService.search(moduleNames, searchTerm,
                        startTime, endTime, 0, 0, types, negateQuery, null, null);
                }
            }

            this.resultSize = results.getTotalNumberOfResults();
            this.queryTime = results.getQueryResponseTime();

            this.resultsLabel.setText(String.format(getTranslation("label.search-results-returned",
                UI.getCurrent().getLocale(), null), this.resultSize, this.queryTime));
            this.resultsLabel.getElement().getStyle().set("fontSize", "10pt");

            return (int) results.getTotalNumberOfResults();
        });

        filteredDataProvider = dataProvider.withConfigurableFilter();
        filteredDataProvider.setFilter(this.searchFilter);

        this.setDataProvider(filteredDataProvider);
    }

    private IkasanSolrDocumentSearchResults getResults(IkasanAuthentication authentication, SearchFilter filter, long startTime
        , long endTime, String searchTerm, int offset, int limit, List<String> types, boolean negateQuery, String sortField, String sortOrder)
    {
        Set<String> moduleNames = null;

        if(filter.getModuleNamesFilter() != null && !filter.getModuleNamesFilter().isEmpty())
        {
            moduleNames = new HashSet<>();

            if(!authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            {
                Set<String> allowedModuleNames = SecurityUtils.getAccessibleModules(authentication);

                moduleNames.addAll(allowedModuleNames.stream()
                    .filter(name -> filter.getModuleNamesFilter()
                        .stream()
                        .anyMatch(moduleName -> moduleName.startsWith(name)))
                    .collect(Collectors.toList()));

                if(moduleNames.isEmpty()){
                    moduleNames.add("--");
                }
            }
            else
            {
                moduleNames = filter.getModuleNamesFilter()
                    .stream()
                    .map(moduleName -> "*" + ClientUtils.escapeQueryChars(moduleName) + "*")
                    .collect(Collectors.toSet());
            }
        }

        Set<String> flowNames = null;

        if(filter.getFlowNamesFilter() != null && !filter.getFlowNamesFilter().isEmpty())
        {
            flowNames = filter.getFlowNamesFilter()
                .stream()
                .map(flowName -> "*" + ClientUtils.escapeQueryChars(flowName) + "*")
                .collect(Collectors.toSet());
        }

        HashSet<String> componentNames = null;

        if(filter.getComponentNameFilter() != null && !filter.getComponentNameFilter().isEmpty())
        {
            componentNames = new HashSet<>();
            componentNames.add("*" + ClientUtils.escapeQueryChars(filter.getComponentNameFilter()) + "*");
        }

        String eventId = null;

        if(filter.getEventIdFilter() != null && !filter.getEventIdFilter().isEmpty())
        {
            eventId = "*" + ClientUtils.escapeQueryChars(filter.getEventIdFilter()) + "*";
        }

        if(!authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY) && moduleNames == null)
        {
            moduleNames = SecurityUtils.getAccessibleModules(authentication);

            if(moduleNames.isEmpty()){
                moduleNames.add("--");
            }
        }

        try {
            return this.solrSearchService.search(moduleNames, flowNames, componentNames, eventId, searchTerm,
                startTime, endTime, offset, limit, types, negateQuery, sortField, sortOrder);
        }
        catch (Exception e) {
            final UI current = UI.getCurrent();
            final I18NProvider i18NProvider = VaadinService.getCurrent().getInstantiator().getI18NProvider();
            NotificationHelper.showErrorNotification(i18NProvider.getTranslation("error.solr-unavailable"
                , current.getLocale()));
        }

        return new IkasanSolrDocumentSearchResults(new ArrayList<>(), 0, 0);
    }

    public long getResultSize()
    {
        return resultSize;
    }
}
