/**
 * Copyright 2013, Real-Time Innovations (RTI)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.rti.dds.camel.shapes;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.rti.dds.type.shape.ShapeType;

/**
 * A bean which we use in the route
 */
public class TransposeShapeCoordinates implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		ShapeType st = exchange.getIn().getBody(ShapeType.class);
		st.shapesize = st.x / 2;
		int temp = st.x;
		st.x = st.y;
		st.y = temp;
	}

}
