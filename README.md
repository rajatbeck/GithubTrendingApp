# GithubTrendingApp
This Application shows the list of trending repositories in github on daily basis.   

*Listing View supports both portrait as well as landsacpe orientation.Also, it consits of functionality like sort by Name or sort by stars. Additionally application fully supports offline mode. Moreover once the data is fetched it is served from the cache until it cache time expires. Pull to refresh functionality is also provided to purge the cache and fetch the latest data. Also, individual row can be expanded or collapsed on click action*  <br /><br />
     <img src="https://github.com/rajatbeck/GithubTrendingApp/blob/master/screenshots/list.jpeg" width="200"> 
              <img src="https://github.com/rajatbeck/GithubTrendingApp/blob/master/screenshots/landscape.jpeg" width="200"> <img src="https://github.com/rajatbeck/GithubTrendingApp/blob/master/screenshots/sort.jpeg" width="200">   <img src="https://github.com/rajatbeck/GithubTrendingApp/blob/master/screenshots/force_cache.jpeg" width="200">
              
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**Portrait**&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**Landscape**&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**Sorting**&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**Refresh**


This Application explore different Android component that can be used to create a simple single page application, Basically the app fetches data from server using <ins>Retrofit</ins> along with <ins>okhttp</ins> and saves it in <ins>Room Database</ins> and then renders the UI. App is completely based on reactive pattern courtesy <ins>Rxjava</ins>. Application code also implements designs patterns like <ins>*Singleton pattern*,*Observer Pattern*,*Builder Pattern*,*Factory pattern*,*Adapter pattern*</ins>.The code also supports Depedendency Injection one of the forms of inversion of control principle(IOC) with the help of <ins>Dagger2</ins>.All the views are created using <ins>Constraint layout</ins> to ensuring a flat view hierarchy.App's complete architecture is in <ins>MVVM</ins> and app also explores concept of <ins>LiveData</ins>. <br /><br />Please refer to [this](https://githubtrendingapi.docs.apiary.io/#reference/0/developers/list-trending-repositories) link for more details regarding github trending API.

This project explores the following concepts in details:    
<ol>
  <li>Kotlin</li>   
  <li>Model-View-View-Model(MVVM)</li>  
  <li>Rxjava2</li>  
  <li>Dagger2</li>    
  <li>Room</li>   
  <li>Retrofit</li>   
<li>Okhttp</li>   
  <li>Moshi</li>  
  <li>Google material deisgn</li>   
  <li>Junit5 and Mockito</li> <br />
  
  <h2>Future Task</h2>
  
  <ul>
  <li>Option to showing trending by program language</li>
  <li>Option to configure list Trending period: daily, weekly, monthly</li>
  <li> UI Test cases </li>
  <li> Implement UniDirectional flow </li>
  <li> Coroutines Support </li>
  </ul>




 
