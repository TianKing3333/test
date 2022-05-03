import request from '@/utils/request'

const api_name = `/api/ucenter/wx`

export default {
  // user模块下
  getLoginParam() {
    return request({
      url: `${api_name}/getLoginParam`,
      method: `get`
    })
  },
    // 生成微信支付二维码
    createNative(orderId) {
      return request({
        url: `/api/order/weixin/createNative/${orderId}`,
        method: 'get'
      })
    },
   // 根据订单号去微信第三方查询支付状态
    queryPayStatus(orderId) {
      return request({
          url: `/api/order/weixin/queryPayStatus/${orderId}`,
          method: 'get'
      })
  },
  
}
