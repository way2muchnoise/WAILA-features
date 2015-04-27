package wailafeatures.util;

import java.util.Map;

public class Tuple<V, W>
{
    private V objV;
    private W objW;

    public Tuple(V objV, W objW)
    {
        this.objV = objV;
        this.objW = objW;
    }

    public Tuple(Map.Entry<V, W> entry)
    {
        this(entry.getKey(), entry.getValue());
    }

    public void setFirst(V obj)
    {
        this.objV = obj;
    }

    public void setSecond(W obj)
    {
        this.objW = obj;
    }

    public V getFirst()
    {
        return objV;
    }

    public W getSecond()
    {
        return objW;
    }
}
