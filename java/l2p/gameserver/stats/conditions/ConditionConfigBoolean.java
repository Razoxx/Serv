package l2p.gameserver.stats.conditions;

import java.lang.reflect.Field;
import l2p.gameserver.stats.Env;

public class ConditionConfigBoolean
  extends Condition
{
  private final Field _config;
  private final boolean _value;
  
  public ConditionConfigBoolean(Field field, boolean value)
  {
    this._config = field;
    this._value = value;
  }
  
  protected boolean testImpl(Env env)
  {
    try
    {
      return this._config.getBoolean(null) == this._value;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return false;
  }
}
