/**
 *
 * Copyright (c) 2014, the Railo Company Ltd. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
package lucee.runtime.gateway;

import lucee.runtime.exp.PageException;
import lucee.runtime.exp.PageExceptionBox;

public class PageGatewayException extends GatewayException implements PageExceptionBox {

	private static final long serialVersionUID = 752599325554487824L;
	private PageException pe;

	public PageGatewayException(PageException pe) {
		super(pe.getMessage());
		this.pe = pe;
		initCause(pe);

	}

	@Override
	public PageException getPageException() {
		return pe;
	}

}