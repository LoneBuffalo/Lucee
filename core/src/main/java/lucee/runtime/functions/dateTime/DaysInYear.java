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
 * Implements the CFML Function daysinyear
 */
package lucee.runtime.functions.dateTime;

import java.util.TimeZone;

import lucee.commons.date.DateTimeUtil;
import lucee.runtime.PageContext;
import lucee.runtime.exp.PageException;
import lucee.runtime.ext.function.BIF;
import lucee.runtime.op.Caster;
import lucee.runtime.type.dt.DateTime;

public final class DaysInYear extends BIF {

	private static final long serialVersionUID = -2900647153777735688L;

	public static Number call(PageContext pc, DateTime date) {
		return _call(pc, date, pc.getTimeZone());
	}

	public static Number call(PageContext pc, DateTime date, TimeZone tz) {
		return _call(pc, date, tz == null ? pc.getTimeZone() : tz);
	}

	private static Number _call(PageContext pc, DateTime date, TimeZone tz) {
		DateTimeUtil util = DateTimeUtil.getInstance();
		return Caster.toNumber(pc, util.isLeapYear(util.getYear(tz, date)) ? 366 : 365);
	}

	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if (args.length == 1) return call(pc, Caster.toDatetime(args[0], pc.getTimeZone()));
		return call(pc, Caster.toDatetime(args[0], pc.getTimeZone()), Caster.toTimeZone(args[1]));
	}
}