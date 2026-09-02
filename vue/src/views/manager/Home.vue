
<template>
  <div>
    <div class="card" v-if="data.user.role === 'USER'">您好同学！欢迎使用本系统，祝您毕业顺利！</div>
    <div class="card" style="margin-top: 10px;" v-if="data.user.role === 'USER'">
      <div style="font-size: 18px; margin-bottom: 20px">系统公告</div>
      <el-timeline style="max-width: 600px">
        <el-timeline-item :timestamp="item.time" color="#0bbd87" placement="top" v-for="item in data.goodsData">
            <h4>{{item.title}}</h4>
            <p>{{item.content}}</p>
        </el-timeline-item>
      </el-timeline>
    </div>
    <div v-else>
      <div style="display: flex; grid-gap: 10px">
        <div class="card" style="height: 400px;width: 50%" id="pie"></div>
        <div class="card" style="height: 400px;width: 50%" id="bar"></div>
      </div>
      <div class="card" style="height: 400px;width: 100%;margin-top: 10px" id="line"></div>
    </div>
  </div>
</template>

<script setup>
import {reactive,onMounted} from "vue";
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import * as echarts from 'echarts';

const data = reactive({
      user: JSON.parse(localStorage.getItem('code_user') || '{}'),
      goodsData: []
    }
)

const loadNotice = () =>{
  request.get('/goods/selectAll').then(res =>{
    if (res.code === '200'){
      data.goodsData =res.data
      if(data.goodsData.length > 3){
        data.goodsData =data.goodsData.slice(0,3)
      }
    } else {
      ElMessage.error(res.msg)
    }
  })
}
loadNotice()

const loadPie = () =>{
  request.get('/echarts/pie').then(res =>{
    if (res.code === '200'){
      let chartDom = document.getElementById('pie');
      let myChart = echarts.init(chartDom);
      pieOptions.series[0].data = res.data
      myChart.setOption(pieOptions)
    }
  })
}

const loadBar = () =>{
  request.get('/echarts/bar').then(res =>{
    if (res.code === '200'){
      let chartDom = document.getElementById('bar');
      let myChart = echarts.init(chartDom);
      barOptions.xAxis.data = res.data.xAxis
      barOptions.series[0].data = res.data.yAxis
      myChart.setOption(barOptions)
    }
  })
}

const loadLine = () =>{
  request.get('/echarts/line').then(res =>{
    if (res.code === '200'){
      let chartDom = document.getElementById('line');
      let myChart = echarts.init(chartDom);
      lineOptions.xAxis.data = res.data.xAxis
      lineOptions.series[0].data = res.data.yAxis
      myChart.setOption(lineOptions)
    }
  })
}

onMounted( () =>{
  loadPie()
  loadBar()
  loadLine()
})

//饼图
let pieOptions ={
    title: {
      text: '不同分类下用户发布商品的数量',//主标题
      subtext: '统计维度：商品分类',//副标题
      left: 'center'
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b} : {c} ({d}%)',
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '数量占比',
        type: 'pie',
        radius: '50%',
        center: ['50%', '60%'],
        data: [
          { value: 1048, name: 'Search Engine' },
          { value: 735, name: 'Direct' },
          { value: 580, name: 'Email' },
          { value: 484, name: 'Union Ads' },
          { value: 300, name: 'Video Ads' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]

};

//柱状图
let barOptions = {
  title: {
    text: '不同用户发布商品数量Top5',//主标题
    subtext: '统计维度：用户昵称',//副标题
    left: 'center'
  },
  grid:{             // 距离底部的距离
    top: '20%',
    bottom: '10%'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  xAxis: {
    type: 'category',
    data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
    name: '用户昵称',
    axisLabel:{
      show:true,
      interval: 0,
      rotate:-60,
      inside:false,
      margin:10
    },

  },
  yAxis: {
    type: 'value',
    name: '商品数量'
  },
  tooltip: {
    trigger: 'item',
  },
  series: [
    {
      data: [120, 200, 150, 80, 70, 110, 130],
      type: 'bar',
      itemStyle:{
        normal:{
          color:function (){
            return "#" +Math.floor(Math.random()*(256 * 256 * 256 -1)).toString(16)
          }
        }
      }
    }
  ]
};

//折线图
let lineOptions = {
  title: {
    text: '最近一周每天平台用户发布的商品数量',//主标题
    subtext: '统计维度：最近一周',//副标题
    left: 'center'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  grid:{             // 距离底部的距离
    left: '3%',
    right: '4%',
    top: '30%',
    bottom: '2%',
    containLabel: true
  },
  tooltip: {
    trigger: 'item',
  },
  xAxis: {
    type: 'category',
    data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
    name: '日期'
  },
  yAxis: {
    type: 'value',
    name: '攻略数量'
  },
  series: [
    {
      data: [150, 230, 224, 218, 135, 147, 260],
      type: 'line',
      smooth: true,
      markLine:{
        data:[{type: 'average', name: '最近一周攻略发布数量平均值'}],
      },
      markPoint:{
        data:[
          {type: 'max',name: '最大值'},
          {type: 'min',name: '最小值'},
        ]
      }
    }
  ]
};

</script>
<style scoped>

</style>