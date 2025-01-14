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
 * Implements the CFML Function createtimespan
 */
package lucee.runtime.functions.dateTime;

import lucee.runtime.PageContext;
import lucee.runtime.ext.function.Function;
import lucee.runtime.op.Caster;
import lucee.runtime.type.dt.TimeSpan;
import lucee.runtime.type.dt.TimeSpanImpl;

public final class CreateTimeSpan implements Function {
	private static final long serialVersionUID = -5518000993498260249L;

	public static TimeSpan call(PageContext pc, Number day, Number hour, Number minute, Number second) {
		return new TimeSpanImpl(Caster.toIntValue(day), Caster.toIntValue(hour), Caster.toIntValue(minute), Caster.toIntValue(second));
	}

	public static TimeSpan call(PageContext pc, Number day, Number hour, Number minute, Number second, Number millisecond) {
		return new TimeSpanImpl(Caster.toIntValue(day), Caster.toIntValue(hour), Caster.toIntValue(minute), Caster.toIntValue(second), Caster.toIntValue(millisecond));
	}
}