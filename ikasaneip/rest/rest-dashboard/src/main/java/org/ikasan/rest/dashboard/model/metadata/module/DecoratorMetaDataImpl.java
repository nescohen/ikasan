package org.ikasan.rest.dashboard.model.metadata.module;

import org.ikasan.spec.metadata.DecoratorMetaData;

public class DecoratorMetaDataImpl implements DecoratorMetaData
{
    private String type;
    private String name;
    private String configurationId;
    private boolean isConfigurable = false;

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String getType()
    {
        return type;
    }

    @Override
    public void setType(String type)
    {
        this.type = type;
    }

    @Override
    public boolean isConfigurable()
    {
        return isConfigurable;
    }

    @Override
    public void setConfigurable(boolean configurable)
    {
        this.isConfigurable = configurable;
    }

    @Override
    public String getConfigurationId()
    {
        return this.configurationId;
    }

    @Override
    public void setConfigurationId(String configurationId)
    {
        this.configurationId = configurationId;
    }
}
