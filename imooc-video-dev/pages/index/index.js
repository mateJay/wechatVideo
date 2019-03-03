const app = getApp()

Page({
  data: {
    //用于分页的属性
    totalPage:1,
    page:1,
    videoList:[],

    screenWidth: 350,
    serverUrl:app.serverUrl,
    searchContent:''
  },

  onLoad: function (params) {
    var me = this;
    var screenWidth = wx.getSystemInfoSync().screenWidth;
    

    var isSaveRecord = params.isSaveRecord;
    var searchContent = params.search;
    // console.log(searchContent);
    if (searchContent == undefined){
      searchContent = '';
    }
    me.setData({
      screenWidth: screenWidth,
      searchContent: searchContent
    });

    if (isSaveRecord != 1){
      isSaveRecord = 0;
    }

    var page = me.data.page;

    me.getAllVideoList(page, isSaveRecord);
    
  },

  getAllVideoList: function (page, isSaveRecord){
    var me = this;

    var serverUrl = app.serverUrl;
    wx.showLoading({
      title: '请等待，正在加载~',
    });

    var searchContent = me.data.searchContent;
    wx.request({
      url: serverUrl + '/video/showAll?page=' + page + '&isSaveRecord=' + isSaveRecord,
      data:{
        videoDesc: searchContent
      },
      method: 'POST',
      success: function (res) {
        wx.hideLoading();
        wx.hideNavigationBarLoading();
        wx.stopPullDownRefresh();
        // console.log(res.data);

        if (page == 1) {
          me.setData({
            videoList: [],
          });
        }
        var videoList = res.data.data.rows;
        var newVideoList = me.data.videoList;

        me.setData({
          videoList: newVideoList.concat(videoList),
          page: page,
          serverUrl: serverUrl,
          totalPage: res.data.data.total
        })
      }
    })


  },
  onReachBottom:function(){
    var me = this;
    var currentPage = me.data.page;
    var totalPage = me.data.totalPage;
    // console.log(currentPage+"----"+totalPage);

    if(currentPage == totalPage){
      // console.log("到底了。。。")
      wx.showToast({
        title: '已经没有视频了~~',
        icon:'none'
      })
      return;
    }

    var page = currentPage + 1;
    me.getAllVideoList(page,0);
  },
  //下拉刷新
  onPullDownRefresh:function(){
    wx.showNavigationBarLoading();
    this.getAllVideoList(1,0);
  },
  showVideoInfo:function(e){
    var me = this;
    var videoList = me.data.videoList;
    // console.log(e);
    var arrindex = e.target.dataset.arrindex;
    var videoInfo = JSON.stringify(videoList[arrindex]);

    wx.navigateTo({
      url: '../videoinfo/videoinfo?videoInfo=' + videoInfo,
    })
  }

})
