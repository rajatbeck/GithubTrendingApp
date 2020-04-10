package com.rajat.zomatotest.repository

import com.rajat.zomatotest.models.Repository
import com.rajat.zomatotest.models.Resource
import com.rajat.zomatotest.repository.local.RepositoryDAO
import com.rajat.zomatotest.repository.remote.GithubService
import com.rajat.zomatotest.utils.InstantExecutorExtension
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.reactivex.Flowable
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.*

@ExtendWith(InstantExecutorExtension::class)
class GithubRepositoryTest {

    private lateinit var githubRepository: GithubRepository

    @Mock
    private lateinit var dao:RepositoryDAO

    @Mock
    private lateinit var service: GithubService

    @BeforeEach
    public fun initEach(){
        MockitoAnnotations.initMocks(this)
        githubRepository = GithubRepository(dao,service)
    }

    /**
     *
     *  Case 1: We get the response from the network but DB is empty,
     *  so we return the same list back.
     *
     */
    @Test
    fun uniqueMergeList_returnSameList(){

        val successInsert = Resource.Success(getTrendingRepoInGitTest())
        val dbListResource = Resource.Success(listOf<Repository>())
        val networkListResource = Resource.Success(getTrendingRepoInGitTest())

        val returnedObserver = githubRepository.uniqueMergeList(dbListResource,networkListResource).test()

        returnedObserver.assertValue {
           it.data?.equals(successInsert.data)?: false
        }

        returnedObserver.dispose()

    }

    companion object{

        fun getTrendingRepoInGitTest():List<Repository>{
            val trendingRepo = "[\n" +
                    "  {\n" +
                    "    \"author\": \"xingshaocheng\",\n" +
                    "    \"name\": \"architect-awesome\",\n" +
                    "    \"avatar\": \"https://github.com/xingshaocheng.png\",\n" +
                    "    \"url\": \"https://github.com/xingshaocheng/architect-awesome\",\n" +
                    "    \"description\": \"后端架构师技术图谱\",\n" +
                    "    \"language\": \"Go\",\n" +
                    "    \"languageColor\": \"#3572A5\",\n" +
                    "    \"stars\": 7333,\n" +
                    "    \"forks\": 1546,\n" +
                    "    \"currentPeriodStars\": 1528,\n" +
                    "    \"builtBy\": [\n" +
                    "      {\n" +
                    "        \"href\": \"https://github.com/viatsko\",\n" +
                    "        \"avatar\": \"https://avatars0.githubusercontent.com/u/376065\",\n" +
                    "        \"username\": \"viatsko\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"author\": \"google\",\n" +
                    "    \"name\": \"gvisor\",\n" +
                    "    \"avatar\": \"https://github.com/google.png\",\n" +
                    "    \"url\": \"https://github.com/google/gvisor\",\n" +
                    "    \"description\": \"Container Runtime Sandbox\",\n" +
                    "    \"language\": \"Go\",\n" +
                    "    \"languageColor\": \"#3572A5\",\n" +
                    "    \"stars\": 3346,\n" +
                    "    \"forks\": 118,\n" +
                    "    \"currentPeriodStars\": 1624,\n" +
                    "    \"builtBy\": [\n" +
                    "      {\n" +
                    "        \"href\": \"https://github.com/viatsko\",\n" +
                    "        \"avatar\": \"https://avatars0.githubusercontent.com/u/376065\",\n" +
                    "        \"username\": \"viatsko\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"author\": \"davideuler\",\n" +
                    "    \"name\": \"architecture.of.internet-product\",\n" +
                    "    \"avatar\": \"https://github.com/davideuler.png\",\n" +
                    "    \"url\": \"https://github.com/davideuler/architecture.of.internet-product\",\n" +
                    "    \"description\": \"互联网公司技术架构，微信/淘宝/腾讯/阿里/美团点评/百度/微博/Google/Facebook/Amazon/eBay的架构，欢迎PR补充\",\n" +
                    "    \"language\": \"Go\",\n" +
                    "    \"languageColor\": \"#3572A5\",\n" +
                    "    \"stars\": 2763,\n" +
                    "    \"forks\": 416,\n" +
                    "    \"currentPeriodStars\": 1427,\n" +
                    "    \"builtBy\": [\n" +
                    "      {\n" +
                    "        \"href\": \"https://github.com/viatsko\",\n" +
                    "        \"avatar\": \"https://avatars0.githubusercontent.com/u/376065\",\n" +
                    "        \"username\": \"viatsko\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"author\": \"kusti8\",\n" +
                    "    \"name\": \"proton-native\",\n" +
                    "    \"avatar\": \"https://github.com/kusti8.png\",\n" +
                    "    \"url\": \"https://github.com/kusti8/proton-native\",\n" +
                    "    \"description\": \"A React environment for cross platform native desktop apps\",\n" +
                    "    \"language\": \"JavaScript\",\n" +
                    "    \"languageColor\": \"#3572A5\",\n" +
                    "    \"stars\": 4711,\n" +
                    "    \"forks\": 124,\n" +
                    "    \"currentPeriodStars\": 1186,\n" +
                    "    \"builtBy\": [\n" +
                    "      {\n" +
                    "        \"href\": \"https://github.com/viatsko\",\n" +
                    "        \"avatar\": \"https://avatars0.githubusercontent.com/u/376065\",\n" +
                    "        \"username\": \"viatsko\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  }\n" +
                    "]"
            val listType = Types.newParameterizedType(List::class.java,
                Repository::class.java)
            val jsonAdapter = Moshi.Builder().build().adapter<List<Repository>>(listType)
            return jsonAdapter.fromJson(trendingRepo)?: listOf()
        }

    }
}