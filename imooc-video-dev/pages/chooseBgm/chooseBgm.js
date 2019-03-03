const app = getApp()

Page({
    data: {
      bgmList: [],
      serverUrl: app.serverUrl,
      videoParams: {}
    },

    onLoad: function (params) {
      var me = this;

      me.setData({
        videoParams:params
      });

      wx.showToast({
        title: 'please wait~~',
      });

      //从缓存中获取全局变量
      var userInfo = app.getglobalUserInfo();
      var serverUrl = app.serverUrl;
      //
      wx.request({
        url: serverUrl+'/bgm/list',
        method:'POST',
        header: {
          'content-type': 'application/json', // 默认值
          'userId': userInfo.id,
          'userToken': userInfo.userToken
        },
        success(res) {
          wx.hideToast();

          console.log(res.data);
          if(res.data.status == 200){
            me.setData({
              bgmList:res.data.data
            })
          }
        }
      })
      
    },

    upload: function(e) {
      var me = this;
      var serverUrl = app.serverUrl;

      var bgmId = e.detail.value.bgmId;
      var desc = e.detail.value.desc;
      console.log(bgmId + desc);

      var duration = me.data.videoParams.duration;
      var tmpHeight = me.data.videoParams.tmpHeight;
      var tempWidth = me.data.videoParams.tempWidth;
      var tmpVideoUrl = me.data.videoParams.tmpVideoUrl;
      var tmpCoverUrl = me.data.videoParams.tmpCoverUrl;

      wx.showLoading({
        title: '上传中。。。',
      })

//从缓存中获取全局变量
      var userInfo = app.getglobalUserInfo();
      console.log(userInfo);
      //上传视频
      wx.uploadFile({
        url: serverUrl + '/video/upload',
        header: {
          'content-type': 'application/json', // 默认值
          'userId': userInfo.id,
          'userToken': userInfo.userToken
        },
        formData: {
          userId: userInfo.id,
          bgmId: bgmId,
          videoSeconds: duration,
          videoWidth: tempWidth,
          videoHeight: tmpHeight,
          desc: desc
        },
        filePath: tmpVideoUrl,
        name: 'file',
        success: function (res) {   
          wx.hideLoading();
          var data = JSON.parse(res.data);
          console.log(data);
          var videoId = data.data;

          if (data.status == 200) {
            wx.showToast({
              title: '上传成功',
              icon:'success'
            });
            wx.navigateBack({
              delta : 1,
            });
            //上传封面
            // wx.uploadFile({
            //   url: serverUrl + '/video/uploadCover',
            //   formData: {
            //     userId: app.userInfo.id,
            //     videoId: videoId,
            //   },
            //   filePath: tmpCoverUrl,
            //   name: 'file',
            //   success: function (res) {
            //     var data = JSON.parse(res.data);
            //     if(data.status == 200){
            //       wx.showToast({
            //         title: '上传成功~~',
            //         icon:'success'
            //       });

            //       wx.navigateBack({
            //         delta : 1,
            //       });

            //     }else{
            //       wx.showToast({
            //         title: '上传失败。。',
            //       })
            //     }
            //   }
            // })
          }else{
            wx.showToast({
              title: '上传失败',
            })
          }

        }
      })

    }
})

