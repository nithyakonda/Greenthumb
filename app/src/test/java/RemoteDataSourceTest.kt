import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nkonda.greenthumb.data.source.remote.RemoteDataSource
import kotlinx.coroutines.Dispatchers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class RemoteDataSourceTest {

    private lateinit var remoteDataSource: RemoteDataSource

    @Before
    fun setup() {
        remoteDataSource = RemoteDataSource(Dispatchers.Main)
    }

    @Test
    fun searchPlantByName() {
    }


    fun getPlantById() {
    }
}