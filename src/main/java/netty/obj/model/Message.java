package netty.obj.model;

/**
 * Created by hzyouzhihao on 2016/9/12.
 */
public class Message {

    private int length;

    private String content;
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public int getLength() {
        return length;
    }
    public void setLength(int length) {
        this.length = length;
    }
}
