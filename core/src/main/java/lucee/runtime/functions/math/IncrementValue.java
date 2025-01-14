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
/**
 * Implements the CFML Function incrementvalue
 */
package lucee.runtime.functions.math;

import java.math.BigDecimal;

import lucee.runtime.PageContext;
import lucee.runtime.engine.ThreadLocalPageContext;
import lucee.runtime.ext.function.Function;
import lucee.runtime.op.Caster;

public final class IncrementValue implements Function {

	private static final long serialVersionUID = 4690923533697203403L;

	public static double call(PageContext pc, double number) {
		return ++number;
	}

	public static Number call(PageContext pc, Number number) {
		if (ThreadLocalPageContext.preciseMath(pc)) {
			return Caster.toBigDecimal(number).add(BigDecimal.ONE);
		}
		return number.doubleValue() + 1;
	}
}