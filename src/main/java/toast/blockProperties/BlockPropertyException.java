package toast.blockProperties;

public class BlockPropertyException extends RuntimeException
{
    public BlockPropertyException(String comment, String path) {
        super(comment + " at " + path);
    }
    
    public BlockPropertyException(String comment, String path, Exception ex) {
        super(comment + " at " + path, ex);
    }
}