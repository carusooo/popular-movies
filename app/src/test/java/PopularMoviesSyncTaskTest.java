import android.content.ContentResolver;

import com.example.macarus0.popularmovies.sync.PopularMoviesSyncTask;
import com.example.macarus0.popularmovies.util.MovieJSONUtilities;
import com.example.macarus0.popularmovies.util.NetworkUtils;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PopularMoviesSyncTaskTest {

    @Mock
    private ContentResolver mockContentResolver;
    @Mock
    private MovieJSONUtilities mockJsonUtilities;
    @Mock
    private NetworkUtils mockNetworkUtils;


    @Test
    public void popularMoviesSyncTask() {
        PopularMoviesSyncTask.syncMovies(mockContentResolver, mockNetworkUtils,
                mockJsonUtilities, null);

        verify(mockNetworkUtils, times(1)).getPopularMoviesUrl();
        verify(mockNetworkUtils, times(1)).getTopRatedMoviesUrl();

        verify(mockNetworkUtils, never()).getMovieDetailsUrl(anyString());


    }

    @Test
    public void movieDetailsSyncTask() {
        String movieID = "movieId";
        PopularMoviesSyncTask.syncMovies(mockContentResolver, mockNetworkUtils,
                mockJsonUtilities, movieID);

        verify(mockNetworkUtils, times(1)).getMovieDetailsUrl(movieID);

        verify(mockNetworkUtils, never()).getPopularMoviesUrl();
        verify(mockNetworkUtils, never()).getTopRatedMoviesUrl();

    }
}
