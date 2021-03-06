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
package org.ikasan.error.reporting.model;

import java.io.Serializable;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class ErrorOccurrenceLinkPk implements Serializable
{
	private static final long serialVersionUID = -1724759502309436272L;

	private String errorUri;
	private Long linkId;
	
	/**
	 * @param errorUri
	 * @param linkId
	 */
	public ErrorOccurrenceLinkPk(String errorUri, Long linkId)
	{
		super();
		this.errorUri = errorUri;
		this.linkId = linkId;
	}
	
	private ErrorOccurrenceLinkPk()
	{
	}
	
	/**
	 * @return the errorUri
	 */
	public String getErrorUri()
	{
		return errorUri;
	}
	
	/**
	 * @param errorUri the errorUri to set
	 */
	public void setErrorUri(String errorUri)
	{
		this.errorUri = errorUri;
	}
	
	/**
	 * @return the linkId
	 */
	public Long getLinkId()
	{
		return linkId;
	}
	
	/**
	 * @param linkId the linkId to set
	 */
	public void setLinkId(Long linkId)
	{
		this.linkId = linkId;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((errorUri == null) ? 0 : errorUri.hashCode());
		result = prime * result + ((linkId == null) ? 0 : linkId.hashCode());
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ErrorOccurrenceLinkPk other = (ErrorOccurrenceLinkPk) obj;
		if (errorUri == null)
		{
			if (other.errorUri != null)
				return false;
		} else if (!errorUri.equals(other.errorUri))
			return false;
		if (linkId == null)
		{
			if (other.linkId != null)
				return false;
		} else if (!linkId.equals(other.linkId))
			return false;
		return true;
	}
	
	
}
