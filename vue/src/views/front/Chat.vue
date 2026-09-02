<template>
  <div class="chat-page">
    <div class="card1">
      <div class="chat-container">

        <!-- 左侧用户列表 -->
        <div class="chat-left">
          <div class="chat-left-header">
            <span class="go-back" @click="goBack">&#10094;</span>
            <span class="chat-title">聊天列表</span>
            <span class="chat-count">{{ users.length }}</span>
          </div>
          <div class="chat-session-list">
            <div
                v-for="item in users"
                :key="item.id"
                class="session-item"
                :class="{ active: chatUser?.id === item.id }"
                @click="selectUser(item)"
            >
              <div class="avatar-wrapper">
                <img :src="item.avatar" class="avatar" />
                <span v-if="item.count > 0" class="badge">{{ item.count }}</span>
                <span class="status-dot" :class="{ online: item.online, offline: !item.online }"></span>
              </div>
              <div class="session-info">
                <div class="name">{{ item.name }}</div>
                <div class="status">{{ item.online ? '在线' : '离线' }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 中间分隔线 -->
        <div class="divider-vertical"></div>

        <!-- 右侧聊天区 -->
        <div class="chat-right" v-if="chatUser?.id">
          <div class="chat-right-header">{{ chatUser.name }}</div>
          <div class="divider-horizontal"></div>

          <div class="chat-body" ref="chatBox">
            <div
                v-for="msg in messages"
                :key="msg.id || msg.time"
                :class="['msg', msg.fromUserId === user.id ? 'me' : 'other']"
            >
              <img
                  class="msg-avatar"
                  :src="msg.fromUserId === user.id ? user.avatar : chatUser.avatar"
              />

              <div class="msg-content">
                <!-- 文字消息 -->
                <div v-if="msg.type === '文字'" class="bubble">
                  {{ msg.text }}
                </div>

                <!-- 图片消息 -->
                <el-image
                    v-else-if="msg.type === '图片'"
                    class="img-message"
                    :src="msg.text"
                    :preview-src-list="[msg.text]"
                    fit="cover"
                />

                <div class="msg-time">{{ msg.time }}</div>
              </div>
            </div>
          </div>

          <div class="divider-horizontal"></div>

          <div class="chat-input">
            <div class="tools">
              <el-upload
                  action="http://localhost:9999/files/upload"
                  :show-file-list="false"
                  :on-success="sendImgMessage"
              >
                <el-button type="default" :icon="UploadFilled" >图片</el-button>

              </el-upload>
            </div>
            <textarea
                v-model="text"
                placeholder="请输入消息消息内容"
                @keydown="handleKeydown"
                @focus="onInputFocus"

            ></textarea>
            <div class="input-footer">
              <span class="shortcut">
                <el-icon><Position/></el-icon>
                按 Ctrl+Enter 发送
              </span>
              <button class="send-btn"
                      @click="sendMessage"
                      :class="{ disabled: !chatUser?.id || !text }"
                      :disabled="!chatUser?.id || !text"
              >
                发送
              </button>
            </div>
          </div>
        </div>

        <div class="chat-empty" v-else>
          <div class="empty-box">
            <img src="@/assets/imgs/chat-empty.png" class="empty-img" />
            <div class="empty-text">
              <div class="empty-title">尚未选择任何联系人</div>
              <div class="empty-desc">快去左侧列表聊起来吧~</div>
            </div>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted, nextTick, onBeforeMount, reactive} from 'vue'
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import {useRouter, useRoute} from "vue-router";
import {UploadFilled} from "@element-plus/icons-vue";
import { onBeforeRouteLeave } from 'vue-router';

/** 模拟登录用户 */

const route = useRoute();
const router = useRouter()

const user = JSON.parse(localStorage.getItem('code_user'));


const chatUser = ref([]);
const users = ref([]);
const userIds = ref([]);
const messages = ref([]);
const text = ref('');
const userId = ref(route.query.id);
const chatBox = ref(null);


let socket = null;



/* ---------------- 初始化 ---------------- */

onMounted(async () => {
  loadUserList();
  initWebSocket();
});

/* ---------------- 用户列表 ---------------- */

const loadUserList =  () => {
   request.get("/chat/user").then(res =>{
     users.value = res.data;
     initWebSocket();
     if (userId.value){
       if(users.value.length === 0){
         request.get("/chat/user/"+ userId.value).then(res =>{
           const user = res.data;
           users.value.unshift(res.data);
           selectUser(user);
           initWebSocket()
         })
       }else{
         let user = users.value.find(u => u.id === Number(userId.value));

         if (!user) {
           request.get("/chat/user/"+ userId.value).then(res =>{
             user=res.data;
             users.value.unshift(res.data);
             selectUser(user);
             initWebSocket()
           })
         }else {
           selectUser(user);
         }
       }
     }
   })
}
loadUserList()

const goBack = () => {
  router.go(-1);
}

/* ---------------- 选择用户 ---------------- */

const selectUser = (u) => {
 if(chatUser.value.id !== u.id) {
   chatUser.value = u
   const target = users.value.find(item => item.id === u.id)
   if (target) target.count = 0
   loadMessage(user.id,chatUser.value.id);
 }
}

const loadMessage = (fromUserId,toUserId) => {
  request.get("/chat/message",{
    params: {
      fromUserId:fromUserId,
      toUserId:toUserId
    }
  }).then((res) => {
    messages.value = res.data;
    scrollBottom()
    clear()
  })
}

/* ---------------- WebSocket ---------------- */

const initWebSocket = () => {
  socket = new WebSocket(
      `ws://localhost:9999/chatServer/${user.id}`
  );

  socket.onopen = () => {
    console.log("WebSocket 已连接");

  };

  socket.onerror = (err) => {
    console.error("WebSocket 连接错误", err);
  };

  socket.onmessage = (msg) => {
    const data = JSON.parse(msg.data);

    // 在线广播
    if (data.messageType === "broadcast") {
      userIds.value=data.userIds
      users.value.forEach((item) => {
        item.online = userIds.value.includes(item.id);
      });
    }

    // 聊天消息
    else if (data.messageType === "chat") {
      if (data.fromUserId === chatUser.value.id) {
        const message = {
          text: data.text,
          type: data.type,
          time:data.time,
          fromUserId: data.fromUserId,
          toUserId: data.toUserId,
          isRead:false
        }
        createMessage(message);
      } else {
        let user = users.value.find(u => u.id === Number(data.fromUserId));
        if (user){
          user.count++
        } else {
          request.get("/chat/user/"+ data.fromUserId).then(res =>{
            user=res.data
            user.count =1
            user.online =userIds.value.includes(data.fromUserId);
            users.value.unshift(user);
          })
        }
      }
    }
  };
};

/* ---------------- 发送消息 ---------------- */

const sendMessage = () => {
  if(!chatUser.value.id){
    ElMessage({type:'warning',message:"请选择聊天对象"})
    return
  }
  if (!text.value){
    ElMessage({type:'warning',message:"请输入内容"})
    return
  }

  const message = {
    text: text.value,
    type: '文字',
    time: new Date().toLocaleString('zh-CN'),
    fromUserId: user.id,
    toUserId: chatUser.value.id,
    isRead:false
  };

  socket.send(JSON.stringify(message));
  createMessage(message)
  saveMessage(message)
  text.value=''

};

const sendImgMessage = (res) => {

  const imageUrl = res.data

  const message = {
    text: imageUrl,
    type: '图片',
    time: new Date().toLocaleString('zh-CN'),
    fromUserId: user.id,
    toUserId: chatUser.value.id,
    isRead:false
  };

  socket.send(JSON.stringify(message));
  createMessage(message)
  saveMessage(message)

};

const saveMessage =(message) =>{
  request.post("/chat/add", message).then(res=>{
    clear()
  })
}

const createMessage = (message) => {
  messages.value.push(message)
  scrollBottom()
}

/* ---------------- 滚动到底部 ---------------- */

const scrollBottom = () => {
  nextTick(() => {
    if (chatBox.value) {
      chatBox.value.scrollTop = chatBox.value.scrollHeight;
    }
  });
};

const clear = () =>{
  request.get("/chat/clear", {
   params:{
     fromUserId: user.id,
     toUserId: chatUser.value.id,
   }
  })
}

window.addEventListener('beforeunload', () => {
  if (socket && socket.readyState === WebSocket.OPEN) {
    socket.close();
  }
});



onBeforeRouteLeave((to, from, next) => {
  if (socket && socket.readyState === WebSocket.OPEN) {
    socket.close();
  }
  next();
});

const handleKeydown = (e) => {
  if (e.ctrlKey && e.key === "Enter") {
    sendMessage()
  }
}

const onInputFocus = () => {
  if (chatUser.value?.id) {
    clear()
  }
}




</script>

<style scoped>
.chat-page {
  min-height: calc(100vh - 60px - 60px); /* 60px 头部 + 60px 页脚高度，可按实际调整 */
  background: #f5f5f5;
  padding-bottom: 20px; /* 底部呼吸感 */
  display: flex;
  flex-direction: column;
}

.card1 {
  background: #fff;
  border-radius: 20px;
  margin: 20px auto;
  width: 80%;
  height: 600px; /* 固定高度，也可以改成 min-height */
  display: flex;
  flex-direction: column;
  padding: 0; /* 内部让聊天容器撑满 */

}

.chat-container {
  display: flex;
  flex: 1;
  height: 100%;
}

/* ---------------- 左侧 ---------------- */
.chat-left {
  width: 260px;
  display: flex;
  flex-direction: column;

  border-radius: 10px;
}

.chat-left-header {
  display: flex;
  align-items: center;
  padding: 12px;
  font-weight: bold;
  border-bottom: 1px solid #ddd;
}
.go-back {
  cursor: pointer;
  margin-right: 10px;
}
.chat-title {
  font-weight: bold;
}

.chat-count {
  margin-left: auto;
  background: #409eff;
  color: #fff;
  font-size: 10px;
  padding: 1px 6px;
  border-radius: 12px;
}
.chat-session-list {
  flex: 1;
  overflow-y: auto;

}
.session-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  cursor: pointer;
  position: relative;
  border-radius: 6px;
  margin: 4px;
}
.session-item:hover,
.session-item.active {
  background-color: #e6f7ff;
}
.avatar-wrapper {
  position: relative;
  margin-right: 10px;
}
.avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
}
.badge {
  position: absolute;
  top: -4px;
  right: -4px;
  background: red;
  color: #fff;
  font-size: 12px;
  border-radius: 50%;
  padding: 2px 6px;
}
.status-dot {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 2px solid #fff;
}
.status-dot.online {
  background: #39f03f;
}
.status-dot.offline {
  background: #aaa;
}
.session-info { display: flex;
  flex-direction: column;
  justify-content: center;
}
.name {
  font-weight: 600;
}
.status {
  font-size: 12px;
  color: #666;
}

/* 中间分隔线 */
.divider-vertical { width: 1px; background: #ddd; }

/* 右侧聊天区 */
.chat-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  border-radius: 10px;
  background: #fff;
}
.chat-right-header {
  padding: 12px;
  font-weight: bold;
}
.divider-horizontal {
  height: 1px;
  background: #ddd;
}
.chat-body {
  flex: 1;
  padding: 12px;
  overflow-y: auto;
  background: #fdfcfc;
}
.msg {
  display: flex;
  align-items: flex-start;
  margin-bottom: 14px;
}

.msg.other {
  flex-direction: row;
}

.msg.me {
  flex-direction: row-reverse;
  justify-content: flex-start;
}

/* 头像 */
.msg-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin: 0 8px;
}

/* 内容 */
.msg-content {
  display: inline-flex;
  flex-direction: column;
  align-items: flex-start;
}

.msg.me .msg-content {
  align-items: flex-end;
}

/* 气泡 */
.bubble {
  display: inline-block;
  padding: 8px 12px;
  font-size: 14px;
  border-radius: 10px;
  max-width: 100%;
  word-break: break-word;
  position: relative;
}


.img-message {
  max-width: 220px;
  max-height: 220px;
  display: block;
  cursor: pointer;
  overflow: hidden;
}

.msg.other .img-message {
  border-radius: 4px 12px 12px 12px;
}

.msg.me .img-message {
  border-radius: 12px 4px 12px 12px;
}


/* 左边（对方） */
.msg.other .bubble {
  background: #fff;
  color: #000;
  border: 1px solid #eee;
  border-top-left-radius: 2px; /* 左上角更尖 */
}

/* 右边（自己） */
.msg.me .bubble {
  background: #409eff;
  color: #fff;
  border-top-right-radius: 2px; /* 右上角更尖 */
}

/* 时间 */
.msg-time {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
  text-align: right;
}

.chat-input {
  padding: 8px 12px;
  display: flex;
  flex-direction: column; }
.tools {
  margin-bottom: 6px;
}
textarea {
  width: 100%;
  height: 60px;
  resize: none;
  outline: none;
  padding: 6px;
  border-radius: 6px;
  border: 1px solid #ccc;
  margin-bottom: 6px;
}
.input-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.send-btn {
  background: #ffd666;
  border: none;
  border-radius: 20px;
  padding: 6px 16px;
  cursor: pointer;
  transition: all 0.2s;
}

.send-btn.disabled {
  background: #ffd666;
  color: #999;
  cursor: not-allowed;
}
.shortcut {
  font-size: 12px;
  color: #999;
}

.chat-empty {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.empty-box {
  text-align: center;
}

.empty-img {
  width: 120px;
  margin-bottom: 12px;
}

.empty-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 4px;
}

.empty-desc {
  font-size: 13px;
  color: #999;
}
</style>