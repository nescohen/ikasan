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
package org.ikasan.flow.event;

import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.flow.FlowEvent;

/**
 * Implementation of the EventFactory contract based on the creation 
 * of a FlowEvent.
 * 
 * @author Ikasan Development Team
 *
 */
public class FlowEventFactory implements EventFactory<FlowEvent<?>>
{
    /**
     * Factory method to create a new FlowEvent instance.
     * @param immutable identifier
     * @param mutable payload
     */
    public <PAYLOAD> FlowEvent<?> newEvent(String identifier, PAYLOAD payload)
    {
        return new GenericFlowEvent<PAYLOAD>(identifier, payload);
    }

	/**
	 * Implementation of a flowEvent based on payload being of any generic type.
	 * 
	 * @author Ikasan Development Team
	 *
	 */
	private class GenericFlowEvent<PAYLOAD> implements FlowEvent<PAYLOAD>
	{
		/** immutable identifier */
		private String identifier;

		/** immutable event creation timestamp */
	    private long timestamp;

	    /** payload */
	    private PAYLOAD payload;

        /**
         * Constructor
         * @param identifier2
         */
        protected GenericFlowEvent(String identifier, PAYLOAD payload)
        {
            this.identifier = identifier;
            this.timestamp = System.currentTimeMillis();
            this.payload = payload;
        }
        
		/**
		 * Get immutable flow event identifier.
		 * @return String - event identifier
		 */
		public String getIdentifier()
		{
		    return this.identifier;
		}

		/**
		 * Get the immutable created date/time of the flow event.
		 * @return long - create date time
		 */
		public long getTimestamp()
		{
		    return this.timestamp;
		}

		/**
		 * Get the payload of this flow event.
		 * @return PAYLOAD payload
		 */
		public PAYLOAD getPayload()
		{
		    return this.payload;
		}
		
		/**
		 * Set the payload of this flow event.
		 * @param PAYLOAD - payload
		 */
		public void setPayload(PAYLOAD payload)
		{
		    this.payload = payload;
		}
	}

}
