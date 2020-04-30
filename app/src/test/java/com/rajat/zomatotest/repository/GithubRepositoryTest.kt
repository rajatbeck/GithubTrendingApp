package com.rajat.zomatotest.repository

import com.rajat.zomatotest.models.Repository
import com.rajat.zomatotest.models.Resource
import com.rajat.zomatotest.repository.local.RepositoryDAO
import com.rajat.zomatotest.repository.remote.GithubService
import com.rajat.zomatotest.utils.InstantExecutorExtension
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.reactivex.Single
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@ExtendWith(InstantExecutorExtension::class)
class GithubRepositoryTest {

    private lateinit var githubRepository: GithubRepository

    @Mock
    private lateinit var dao: RepositoryDAO

    @Mock
    private lateinit var service: GithubService

    @BeforeEach
    public fun initEach() {
        MockitoAnnotations.initMocks(this)
        githubRepository = GithubRepository(dao, service)
    }

    /**
     *
     *  Case 1: We get the response from the network but DB is empty,
     *  so we return the same list back.
     *
     */
    @Test
    fun uniqueMergeList_returnSameList() {

        val successInsert = Resource.Success(getTrendingRepoInGitTest())
        val dbListResource = Resource.Success(listOf<Repository>())
        val networkListResource = Resource.Success(getTrendingRepoInGitTest())

        val returnedObserver =
            githubRepository.uniqueMergeList(dbListResource, networkListResource).test()

        returnedObserver.assertValue {
            it.data?.equals(successInsert.data) ?: false
        }
        returnedObserver.dispose()
    }

    /**
     * Case when the local DB result is cached but network
     * gives new data so update the db with new data
     *
     */
    @Test
    fun uniqueMergeList_returnNetworkData() {

        val successInsert = Resource.Success(getTrendingRepo2())
        val dbListResource = Resource.Success(getTrendingRepoInGitTest())
        val networkResource = Resource.Success(getTrendingRepo2())

        val returnedObserver =
            githubRepository.uniqueMergeList(dbListResource, networkResource).test()

        returnedObserver.assertValue {
            it.data?.equals(successInsert.data) ?: false
        }

        returnedObserver.dispose()
    }


    /**
     * Case where db data is old but the new network data fetched is just and
     * updated version of the data and one the row is expanded so the updated data should
     * contains expanded
     *
     */
    @Test
    fun uniqueMergeList_returnUpdatedDbData(){
        val successInsert = Resource.Success(getExpectedResultRepo())
        val dbListResource = Resource.Success(getOneItemInListData())
        val networkResource = Resource.Success(getUpdatedTrendingRepoInGitTest())

        val returnedObserver =
            githubRepository.uniqueMergeList(dbListResource, networkResource).test()

        returnedObserver.assertValue {
            it.data?.equals(successInsert.data) ?: false
        }

        returnedObserver.dispose()
    }


    @Test
    fun getRepositoryListOnce_emptyTest(){

        val emptyList = listOf<Repository>()
        val mockedValue = Single.just(emptyList)
        `when`(dao.getRepositoryListOnce()).thenReturn(mockedValue)

        val returnedValue:Resource<List<Repository>> = githubRepository.getRepositoryListFromDb().blockingGet()

        verify(dao).getRepositoryListOnce()
        verifyNoMoreInteractions(dao)

        Assertions.assertEquals(returnedValue,Resource.Error(EMPTY_TABLE,emptyList))
    }

    companion object {

        fun getOneItemInListData(): List<Repository> {
            val trendingRepo = "[\n" +
                    "  {\n" +
                    "    \"author\": \"xingshaocheng\",\n" +
                    "    \"name\": \"architect-awesome\",\n" +
                    "    \"avatar\": \"https://github.com/xingshaocheng.png\",\n" +
                    "    \"url\": \"https://github.com/xingshaocheng/architect-awesome\",\n" +
                    "    \"description\": \"后端架构师技术图谱\",\n" +
                    "    \"language\": \"Swift\",\n" +
                    "    \"languageColor\": \"#3572A5\",\n" +
                    "    \"stars\": 7333,\n" +
                    "    \"forks\": 1546,\n" +
                    "    \"isExpanded\":true,\n" +
                    "    \"currentPeriodStars\": 1528,\n" +
                    "    \"builtBy\": [\n" +
                    "      {\n" +
                    "        \"href\": \"https://github.com/viatsko\",\n" +
                    "        \"avatar\": \"https://avatars0.githubusercontent.com/u/376065\",\n" +
                    "        \"username\": \"viatsko\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  }\n" +
                    "]"
            val listType = Types.newParameterizedType(
                List::class.java,
                Repository::class.java
            )
            val jsonAdapter = Moshi.Builder().build().adapter<List<Repository>>(listType)
            return jsonAdapter.fromJson(trendingRepo) ?: listOf()
        }

        fun getTrendingRepoInGitTest(): List<Repository> {
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
            val listType = Types.newParameterizedType(
                List::class.java,
                Repository::class.java
            )
            val jsonAdapter = Moshi.Builder().build().adapter<List<Repository>>(listType)
            return jsonAdapter.fromJson(trendingRepo) ?: listOf()
        }

        fun getUpdatedTrendingRepoInGitTest(): List<Repository> {
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
                    "    \"isExpanded\":false,\n" +
                    "    \"currentPeriodStars\": 1528,\n" +
                    "    \"builtBy\": [\n" +
                    "      {\n" +
                    "        \"href\": \"https://github.com/viatsko\",\n" +
                    "        \"avatar\": \"https://avatars0.githubusercontent.com/u/376065\",\n" +
                    "        \"username\": \"viatsko\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  }\n" +
                    "]"
            val listType = Types.newParameterizedType(
                List::class.java,
                Repository::class.java
            )
            val jsonAdapter = Moshi.Builder().build().adapter<List<Repository>>(listType)
            return jsonAdapter.fromJson(trendingRepo) ?: listOf()
        }

        fun getExpectedResultRepo(): List<Repository> {
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
                    "    \"isExpanded\":true,\n" +
                    "    \"currentPeriodStars\": 1528,\n" +
                    "    \"builtBy\": [\n" +
                    "      {\n" +
                    "        \"href\": \"https://github.com/viatsko\",\n" +
                    "        \"avatar\": \"https://avatars0.githubusercontent.com/u/376065\",\n" +
                    "        \"username\": \"viatsko\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  }\n" +
                    "]"
            val listType = Types.newParameterizedType(
                List::class.java,
                Repository::class.java
            )
            val jsonAdapter = Moshi.Builder().build().adapter<List<Repository>>(listType)
            return jsonAdapter.fromJson(trendingRepo) ?: listOf()
        }


        fun getTrendingRepo2(): List<Repository> {
            val trendingRepo2 = "[\n" +
                    "  {\n" +
                    "    \"author\": \"claudiodangelis\",\n" +
                    "    \"name\": \"qrcp\",\n" +
                    "    \"avatar\": \"https://github.com/claudiodangelis.png\",\n" +
                    "    \"url\": \"https://github.com/claudiodangelis/qrcp\",\n" +
                    "    \"description\": \"⚡ Transfer files over wifi from your computer to your mobile device by scanning a QR code without leaving the terminal.\",\n" +
                    "    \"language\": \"Go\",\n" +
                    "    \"languageColor\": \"#00ADD8\",\n" +
                    "    \"stars\": 3879,\n" +
                    "    \"forks\": 202,\n" +
                    "    \"currentPeriodStars\": 280,\n" +
                    "    \"builtBy\": [\n" +
                    "      {\n" +
                    "        \"username\": \"claudiodangelis\",\n" +
                    "        \"href\": \"https://github.com/claudiodangelis\",\n" +
                    "        \"avatar\": \"https://avatars3.githubusercontent.com/u/941963\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"username\": \"brualan\",\n" +
                    "        \"href\": \"https://github.com/brualan\",\n" +
                    "        \"avatar\": \"https://avatars2.githubusercontent.com/u/22512388\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"username\": \"tssva\",\n" +
                    "        \"href\": \"https://github.com/tssva\",\n" +
                    "        \"avatar\": \"https://avatars0.githubusercontent.com/u/20547545\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"username\": \"hellozee\",\n" +
                    "        \"href\": \"https://github.com/hellozee\",\n" +
                    "        \"avatar\": \"https://avatars0.githubusercontent.com/u/12135951\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"username\": \"rafa-acioly\",\n" +
                    "        \"href\": \"https://github.com/rafa-acioly\",\n" +
                    "        \"avatar\": \"https://avatars0.githubusercontent.com/u/11068059\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"author\": \"DP-3T\",\n" +
                    "    \"name\": \"dp3t-app-ios\",\n" +
                    "    \"avatar\": \"https://github.com/DP-3T.png\",\n" +
                    "    \"url\": \"https://github.com/DP-3T/dp3t-app-ios\",\n" +
                    "    \"description\": \"The DP3T-App for iOS\",\n" +
                    "    \"language\": \"Swift\",\n" +
                    "    \"languageColor\": \"#ffac45\",\n" +
                    "    \"stars\": 107,\n" +
                    "    \"forks\": 21,\n" +
                    "    \"currentPeriodStars\": 30,\n" +
                    "    \"builtBy\": [\n" +
                    "      {\n" +
                    "        \"username\": \"maerki\",\n" +
                    "        \"href\": \"https://github.com/maerki\",\n" +
                    "        \"avatar\": \"https://avatars2.githubusercontent.com/u/6221466\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"username\": \"zimmermannubique\",\n" +
                    "        \"href\": \"https://github.com/zimmermannubique\",\n" +
                    "        \"avatar\": \"https://avatars1.githubusercontent.com/u/52426739\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"username\": \"UBaggeler\",\n" +
                    "        \"href\": \"https://github.com/UBaggeler\",\n" +
                    "        \"avatar\": \"https://avatars3.githubusercontent.com/u/35527968\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"username\": \"darthpelo\",\n" +
                    "        \"href\": \"https://github.com/darthpelo\",\n" +
                    "        \"avatar\": \"https://avatars3.githubusercontent.com/u/1339000\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"username\": \"mangerlahn\",\n" +
                    "        \"href\": \"https://github.com/mangerlahn\",\n" +
                    "        \"avatar\": \"https://avatars0.githubusercontent.com/u/11056766\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"author\": \"apache\",\n" +
                    "    \"name\": \"shardingsphere\",\n" +
                    "    \"avatar\": \"https://github.com/apache.png\",\n" +
                    "    \"url\": \"https://github.com/apache/shardingsphere\",\n" +
                    "    \"description\": \"Distributed database middleware\",\n" +
                    "    \"language\": \"Java\",\n" +
                    "    \"languageColor\": \"#b07219\",\n" +
                    "    \"stars\": 10551,\n" +
                    "    \"forks\": 3552,\n" +
                    "    \"currentPeriodStars\": 76,\n" +
                    "    \"builtBy\": [\n" +
                    "      {\n" +
                    "        \"username\": \"tristaZero\",\n" +
                    "        \"href\": \"https://github.com/tristaZero\",\n" +
                    "        \"avatar\": \"https://avatars2.githubusercontent.com/u/27757146\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"username\": \"terrymanu\",\n" +
                    "        \"href\": \"https://github.com/terrymanu\",\n" +
                    "        \"avatar\": \"https://avatars0.githubusercontent.com/u/5516298\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"username\": \"tuohai666\",\n" +
                    "        \"href\": \"https://github.com/tuohai666\",\n" +
                    "        \"avatar\": \"https://avatars2.githubusercontent.com/u/24643893\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"username\": \"cherrylzhao\",\n" +
                    "        \"href\": \"https://github.com/cherrylzhao\",\n" +
                    "        \"avatar\": \"https://avatars0.githubusercontent.com/u/8317649\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"username\": \"haocao\",\n" +
                    "        \"href\": \"https://github.com/haocao\",\n" +
                    "        \"avatar\": \"https://avatars0.githubusercontent.com/u/687732\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  }\n" +
                    "]"
            val listType = Types.newParameterizedType(
                List::class.java,
                Repository::class.java
            )
            val jsonAdapter = Moshi.Builder().build().adapter<List<Repository>>(listType)
            return jsonAdapter.fromJson(trendingRepo2) ?: listOf()

        }

    }
}