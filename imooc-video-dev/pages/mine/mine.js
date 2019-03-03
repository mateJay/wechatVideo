var videoUtil = require('../../utils/videoUtil.js')

const app = getApp()

Page({
  data: {
    faceUrl: "../resource/images/noneface.png",
    nickname:"游客",
    fansCounts:"",
    followCounts:"",
    receiveLikeCounts:"",
    isMe:true,
    isFollow:false,
    publisherId:'',
    userId:'',

    videoSelClass: "video-info",
    isSelectedWork: "video-info-selected",
    isSelectedLike: "",
    isSelectedFollow: "",

    myVideoList: [],
    myVideoPage: 1,
    myVideoTotal: 1,

    likeVideoList: [],
    likeVideoPage: 1,
    likeVideoTotal: 1,

    followVideoList: [],
    followVideoPage: 1,
    followVideoTotal: 1,

    myWorkFalg: false,
    myLikesFalg: true,
    myFollowFalg: true


    },

    onLoad:function(params){
      var me = this;
      // var userInfo = app.userInfo;
      var userInfo = app.getglobalUserInfo();
      // console.log("userInfo:"+userInfo);
      var userId = userInfo.id;
      me.setData({
        userId:userId
      })

      var publisherId = params.publisherId;
      console.log("userId:" + userId + "publisherId" + publisherId);

      if(publisherId != null && publisherId != '' && publisherId != undefined && publisherId != userId){
        userId = publisherId;
        me.setData({
          isMe:false,
          publisherId: publisherId,
          userId:publisherId
        });
      }

      var serverUrl = app.serverUrl;

      wx.request({
        url: serverUrl + '/user/query?userId=' + userId + '&fanId=' + userInfo.id,
        method: "POST",
        header: {
          'content-type': 'application/json', // 默认值
          'userId': userInfo.id,
          'userToken': userInfo.userToken
        },
        success: function (res) {
          console.log(res.data);
          
          if (res.data.status == 200) {
            var userInfo = res.data.data;
            var faceUrl = "../resource/images/noneface.png"; 
            if (userInfo.faceImage != null && userInfo.faceImage != '' 
                && userInfo.faceImage != undefined){
                  faceUrl = serverUrl + userInfo.faceImage;
                }
            me.setData({
              faceUrl: faceUrl,
              fansCounts: userInfo.fansCounts,
              followCounts: userInfo.followCounts,
              receiveLikeCounts: userInfo.receiveLikeCounts,
              nickname: userInfo.nickname,
              isFollow: userInfo.follow
            })
          } else if (res.data.status == 502){
            wx.showToast({
              title: res.data.msg,
              duration:3000,
              icon:'none',
              success:function(){
                wx.redirectTo({
                  url: '../userLogin/login',
                })
              }
            })
          }
        }
      })

      me.getMyVideoList(1);
    },

  logout:function(){
    wx.showLoading({
      title: '请等待...',
    });

    // var user = app.userInfo;
    var user = app.getglobalUserInfo();
    var serverUrl = app.serverUrl;
    wx.request({
      url: serverUrl + '/logout?userId='+user.id,
      method: "POST",
      header: {
        'content-type': 'application/json' // 默认值
      },
      success: function (res) {
        console.log(res.data);
        wx.hideLoading();
        if(res.data.status == 200){

          //app.userInfo = null;
          //注销以后，清空缓存
          wx.removeStorageSync("userInfo")
          wx.showToast({
            title: '注销成功',
            icon: 'success',
            duration: 2000
          });
          //跳转到登陆页面
          wx.redirectTo({
            url: '../userLogin/login',
          })

        }
        
      }
  })
  },
  changFace:function(){
    var me = this;

    wx.chooseImage({
      count: 1,
      sizeType: [ 'compressed'],
      sourceType: ['album'],
      success(res) {
        const tempFilePaths = res.tempFilePaths;
        console.log(tempFilePaths);

        wx.showLoading({
          title: '上传中~~~',
        });
        
        var serverUrl = app.serverUrl;
        var user = app.getglobalUserInfo();

        wx.uploadFile({
          url: serverUrl + '/user/uploadFace?userId=' + user.id,
          header: {
            'content-type': 'application/json', // 默认值
            'userId': user.id,
            'userToken': user.userToken
          },
          filePath: tempFilePaths[0],
          name: 'file',
          success:function(res) {
            wx.hideLoading();

            var data = JSON.parse(res.data);
            console.log(data);

            if(data.status == 200){

              wx.showToast({
                title: '上传成功',
                icon: 'success'
              })

              var imageUrl = data.data;
              me.setData({
                faceUrl: serverUrl + imageUrl
              });


            } else if (data.status == 500){
              wx.showToast({
                title: data.msg
              })
            }
          }
        })
      }
    })
  },
  
  uploadVideo:function(){

    videoUtil.uploadVideo();

  },
  followMe:function(e){
    var me = this;
    var serverUrl = app.serverUrl;
    var publisherId = me.data.publisherId;
    var user = app.getglobalUserInfo();
    var userId = user.id;

    var followType = e.currentTarget.dataset.followtype;
    //1：关注  0：取消关注
    var url = '';
    if(followType == 1){
      url = '/user/beyourFans?userId=' + publisherId +"&fanId=" + userId;
    }else{
      url = '/user/dontyourFans?userId=' + publisherId + "&fanId=" + userId;
    }

    wx.showLoading({
      title: '',
    });
    wx.request({
      url: serverUrl + url,
      method:'POST',
      header: {
        'content-type': 'application/json', // 默认值
        'userId': user.id,
        'userToken': user.userToken
      },

      success:function(res){
        wx.hideLoading();
        if (followType == 1) {
          me.setData({
            isFollow:true,
            fansCounts: ++me.data.fansCounts
          })
        } else {
          me.setData({
            isFollow: false,
            fansCounts: --me.data.fansCounts
          })
        }
      }
    })

  },

  doSelectWork: function () {
    this.setData({
      isSelectedWork: "video-info-selected",
      isSelectedLike: "",
      isSelectedFollow: "",

      myWorkFalg: false,
      myLikesFalg: true,
      myFollowFalg: true,

      myVideoList: [],
      myVideoPage: 1,
      myVideoTotal: 1,

      likeVideoList: [],
      likeVideoPage: 1,
      likeVideoTotal: 1,

      followVideoList: [],
      followVideoPage: 1,
      followVideoTotal: 1
    });
    this.getMyVideoList(1);
  },
  doSelectLike: function () {
    this.setData({
      isSelectedWork: "",
      isSelectedLike: "video-info-selected",
      isSelectedFollow: "",

      myWorkFalg: true,
      myLikesFalg: false,
      myFollowFalg: true,

      myVideoList: [],
      myVideoPage: 1,
      myVideoTotal: 1,

      likeVideoList: [],
      likeVideoPage: 1,
      likeVideoTotal: 1,

      followVideoList: [],
      followVideoPage: 1,
      followVideoTotal: 1
    });
    this.getMyLikesList(1);

  },

  doSelectFollow: function () {
    this.setData({
      isSelectedWork: "",
      isSelectedLike: "",
      isSelectedFollow: "video-info-selected",

      myWorkFalg: true,
      myLikesFalg: true,
      myFollowFalg: false,

      myVideoList: [],
      myVideoPage: 1,
      myVideoTotal: 1,

      likeVideoList: [],
      likeVideoPage: 1,
      likeVideoTotal: 1,

      followVideoList: [],
      followVideoPage: 1,
      followVideoTotal: 1
    });
    this.getMyFollowList(1);
  },

  getMyVideoList: function (page) {
    var me = this;

    // 查询视频信息
    wx.showLoading();
    // 调用后端
    var serverUrl = app.serverUrl;
    wx.request({
      url: serverUrl + '/video/showAll/?page=' + page + '&pageSize=6',
      method: "POST",
      data: {
        userId: me.data.userId
      },
      header: {
        'content-type': 'application/json' // 默认值
      },
      success: function (res) {
        console.log(res.data);
        var myVideoList = res.data.data.rows;
        wx.hideLoading();

        var newVideoList = me.data.myVideoList;
        me.setData({
          myVideoPage: page,
          myVideoList: newVideoList.concat(myVideoList),
          myVideoTotal: res.data.data.total,
          serverUrl: app.serverUrl
        });
      }
    })
  },

  getMyLikesList: function (page) {
    var me = this;
    var userId = me.data.userId;

    // 查询视频信息
    wx.showLoading();
    // 调用后端
    var serverUrl = app.serverUrl;
    wx.request({
      url: serverUrl + '/video/showMyLike/?userId=' + userId + '&page=' + page + '&pageSize=6',
      method: "POST",
      header: {
        'content-type': 'application/json' // 默认值
      },
      success: function (res) {
        console.log(res.data);
        var likeVideoList = res.data.data.rows;
        wx.hideLoading();

        var newVideoList = me.data.likeVideoList;
        me.setData({
          likeVideoPage: page,
          likeVideoList: newVideoList.concat(likeVideoList),
          likeVideoTotal: res.data.data.total,
          serverUrl: app.serverUrl
        });
      }
    })
  },

  getMyFollowList: function (page) {
    var me = this;
    var userId = me.data.userId;

    // 查询视频信息
    wx.showLoading();
    // 调用后端
    var serverUrl = app.serverUrl;
    wx.request({
      url: serverUrl + '/video/showMyFollow/?userId=' + userId + '&page=' + page + '&pageSize=6',
      method: "POST",
      header: {
        'content-type': 'application/json' // 默认值
      },
      success: function (res) {
        console.log(res.data);
        var followVideoList = res.data.data.rows;
        wx.hideLoading();

        var newVideoList = me.data.followVideoList;
        me.setData({
          followVideoPage: page,
          followVideoList: newVideoList.concat(followVideoList),
          followVideoTotal: res.data.data.total,
          serverUrl: app.serverUrl
        });
      }
    })
  },
  // 点击跳转到视频详情页面
  showVideo: function (e) {

    console.log(e);

    var myWorkFalg = this.data.myWorkFalg;
    var myLikesFalg = this.data.myLikesFalg;
    var myFollowFalg = this.data.myFollowFalg;

    if (!myWorkFalg) {
      var videoList = this.data.myVideoList;
    } else if (!myLikesFalg) {
      var videoList = this.data.likeVideoList;
    } else if (!myFollowFalg) {
      var videoList = this.data.followVideoList;
    }

    var arrindex = e.target.dataset.arrindex;
    var videoInfo = JSON.stringify(videoList[arrindex]);

    wx.redirectTo({
      url: '../videoinfo/videoinfo?videoInfo=' + videoInfo
    })

  },

  // 到底部后触发加载
  onReachBottom: function () {

    var myWorkFalg = this.data.myWorkFalg;
    var myLikesFalg = this.data.myLikesFalg;
    var myFollowFalg = this.data.myFollowFalg;

    if (!myWorkFalg) {
      var currentPage = this.data.myVideoPage;
      var totalPage = this.data.myVideoTotal;
      // 获取总页数进行判断，如果当前页数和总页数相等，则不分页
      if (currentPage === totalPage) {
        wx.showToast({
          title: '已经没有视频啦...',
          icon: "none"
        });
        return;
      }
      var page = currentPage + 1;
      this.getMyVideoList(page);
    } else if (!myLikesFalg) {
      var currentPage = this.data.likeVideoPage;
      var totalPage = this.data.myLikesTotal;
      // 获取总页数进行判断，如果当前页数和总页数相等，则不分页
      if (currentPage === totalPage) {
        wx.showToast({
          title: '已经没有视频啦...',
          icon: "none"
        });
        return;
      }
      var page = currentPage + 1;
      this.getMyLikesList(page);
    } else if (!myFollowFalg) {
      var currentPage = this.data.followVideoPage;
      var totalPage = this.data.followVideoTotal;
      // 获取总页数进行判断，如果当前页数和总页数相等，则不分页
      if (currentPage === totalPage) {
        wx.showToast({
          title: '已经没有视频啦...',
          icon: "none"
        });
        return;
      }
      var page = currentPage + 1;
      this.getMyFollowList(page);
    }
  }


})