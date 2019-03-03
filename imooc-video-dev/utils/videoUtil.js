function uploadVideo() {
  var me = this;

  wx.chooseVideo({
    sourceType: ['album'],
    maxDuration: 60,

    success(res) {
      console.log(res);

      var duration = res.duration;
      var tmpHeight = res.height;
      var tempWidth = res.width;
      var tmpVideoUrl = res.tempFilePath;
      var tmpCoverUrl = res.thumbTempFilePath;


      if (duration > 15) {
        wx.showToast({
          title: '视频长度不能大于15秒',
          icon: 'none',
          duration: 2500
        })
      } else {
        // 打开选择BGM界面
        wx.navigateTo({
          url: '../chooseBgm/chooseBgm?duration=' + duration
            + '&tmpHeight=' + tmpHeight
            + '&tempWidth=' + tempWidth
            + '&tmpVideoUrl=' + tmpVideoUrl
            + '&tmpCoverUrl=' + tmpCoverUrl,
        })
      }
    }

  })
}

module.exports = {
  uploadVideo: uploadVideo
}