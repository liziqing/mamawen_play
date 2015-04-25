package actor.message;

/**
 * Created by Ronald on 2015/3/12.
 */
public class GetAnchor {
    public int level;
    public long department;
    public int count;

    public GetAnchor(int level, long department, int count) {
        this.level = level;
        this.department = department;
        this.count = count;
    }
}
