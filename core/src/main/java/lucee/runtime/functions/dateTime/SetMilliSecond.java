package lucee.runtime.functions.dateTime;

import java.util.TimeZone;

import lucee.commons.date.DateTimeUtil;
import lucee.runtime.PageContext;
import lucee.runtime.exp.FunctionException;
import lucee.runtime.exp.PageException;
import lucee.runtime.ext.function.BIF;
import lucee.runtime.op.Caster;
import lucee.runtime.type.dt.DateTime;

public class SetMilliSecond extends BIF {

	private static final long serialVersionUID = -2599246132828267606L;

	public static DateTime call(PageContext pc, DateTime date, Number value) {
		return _call(date, Caster.toIntValue(value), pc.getTimeZone());
	}

	public static DateTime call(PageContext pc, DateTime date, Number value, TimeZone tz) {
		return _call(date, Caster.toIntValue(value), tz == null ? pc.getTimeZone() : tz);
	}

	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if (args.length < 2 || args.length > 3) throw new FunctionException(pc, "SetMilliSecond", 2, 3, args.length);

		TimeZone tz = args.length == 3 ? Caster.toTimeZone(args[2], pc.getTimeZone()) : pc.getTimeZone();
		return _call(Caster.toDate(args[0], tz), Caster.toIntValue(args[1]), tz);

	}

	private static DateTime _call(DateTime date, int value, TimeZone tz) {
		DateTimeUtil.getInstance().setMilliSecond(tz, date, value);
		return date;
	}

}
