package ca.cmpt276.restauranthealthinspection.model.filter;

/**
 * Singleton class for storing filtered results after searching
 */
/* Filter implementation's reference:
 * https://www.youtube.com/redirect?v=sJ-Z9G0SDhc&event=video_description&q=https%3A%2F%2Fcodinginflow.com%2Ftutorials%2Fandroid%2Fsearchview-recyclerview&redir_token=tVrFuM57AHYwXm4Wz_N6gnbE7v18MTU4NTg4OTc0NEAxNTg1ODAzMzQ0
 */
public class MyFilter {
    private static MyFilter instance;

    public static MyFilter getInstance() {
        if (instance == null) {
            return new MyFilter();
        }
        return instance;
    }
}
