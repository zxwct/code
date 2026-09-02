<template>
  <div>
    <div class="card" style="margin-bottom: 5px">
      <el-input clearable @clear="load" style="width: 300px;margin-right: 5px" v-model="data.username" placeholder="请输入账号查询" :prefix-icon="Search"></el-input>
      <el-input clearable @clear="load" style="width: 300px;margin-right: 5px" v-model="data.name" placeholder="请输入名称查询" :prefix-icon="Search"></el-input>
      <el-button type="primary" @click="load">查 询</el-button>
      <el-button type="info" @click="reset">重 置</el-button>
    </div>
    <div class="card" style="margin-bottom: 5px">
      <el-button type="primary" @click="handleAdd">新增</el-button>
      <el-button type="danger" @click="deleteBatch">批量删除</el-button>
      <el-button type="success" @click="exportData">批量导出</el-button>
      <el-upload
          style="display: inline-block; margin-left: 10px"
          action="/api/admin/import"
          :show-file-list="false"
          :on-success="handleImportSuccess"
      >
        <el-button type="info">批量导入</el-button>
      </el-upload>

    </div>
    <div class="card" style="margin-bottom: 5px">
      <el-table :data="data.tableData" style="width: 100%" @selection-change="handleSelectionChange"
                :header-cell-style="{color: '#333',backgroundColor:'#8a9ef1'}">
        <el-table-column type="selection" width="55" />
        <el-table-column label="头像" width="100px" class-name="avatar-column" label-class-name="avatar-column-header">
          <template #default="scope">
            <div class="avatar-cell">
              <el-image v-if="scope.row.avatar" :src="scope.row.avatar" :preview-src-list="[scope.row.avatar]" :preview-teleported="true"
                        style="width: 40px;height: 40px; border-radius: 50%; display: block"/>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="账号" />
        <el-table-column prop="name" label="名称"  />
        <el-table-column prop="phone" label="电话"/>
        <el-table-column prop="email" label="邮箱"/>
        <el-table-column label="操作" width="200px">
            <template #default="scope">
              <el-button type="primary" icon="Edit" circle @click="handleEdit(scope.row)"></el-button>
              <el-button type="danger" icon="Delete" circle @click="del(scope.row.id)"></el-button>
            </template>
        </el-table-column>

      </el-table>
    </div>
    <div class="card">
      <el-pagination
          v-model:current-page="data.pageNum"
          v-model:page-size="data.pageSize"
          layout="total,sizes, prev, pager, next,jumper"
          :page-sizes="[2, 4, 6]"
          :total="data.total"
          @current-change="load"
          @size-change="load"
      />
    </div>

    <el-dialog v-model="data.formVisible" title="管理员信息" width="30%" destroy-on-close>
      <el-form  ref="formRef" :model="data.form" :rules="data.rules" label-width="80px" style="padding: 20px 30px 10px 0">
        <el-form-item prop="username" label="账号" >
          <el-input v-model="data.form.username" autocomplete="off" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item prop="name" label="名称" >
          <el-input v-model="data.form.name" autocomplete="off" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item prop="phone" label="电话">
          <el-input v-model="data.form.phone" autocomplete="off" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item prop="email" label="邮箱">
          <el-input v-model="data.form.email" autocomplete="off" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item prop="avatar" label="头像">
          <el-upload
              action="/api/files/upload"
              :show-file-list="false"
              :headers="{token: data.user.token}"
              :on-success="handleFileSuccess"
              list-type="picture"
              class="avatar-uploader"
          >
            <template #default>
              <div class="avatar-wrapper">
                <!-- 显示头像 -->
                <img
                    v-if="data.form.avatar"
                    :src="data.form.avatar"
                    class="avatar"
                />

                <!-- 删除按钮（默认隐藏，hover 显示） -->
                <el-icon
                    v-if="data.form.avatar"
                    @click.stop="handleDeleteAvatar"
                    class="delete-btn"
                >
                  <Close />
                </el-icon>

                <!-- 如果没有头像，就显示上传按钮 -->
                <el-icon
                    v-else
                    class="avatar-uploader-icon"
                >
                  <Plus />
                </el-icon>
              </div>
            </template>
          </el-upload>


        </el-form-item>

      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="data.formVisible = false">取消</el-button>
          <el-button type="primary" @click="save">
            保 存
          </el-button>
        </div>
      </template>
    </el-dialog>


  </div>

</template>

<script setup>


import {Search} from "@element-plus/icons-vue";
import {reactive,ref} from "vue";
import request from "@/utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";


const data = reactive({
      user: JSON.parse(localStorage.getItem('code_user') || '{}'),
      username:null,
      name: null,
      pageNum: 1,
      pageSize:6,
      total:0,
      tableData:[],
      formVisible:false,
      form: {},
      rules:{
        username:[
          {required: true ,message: '请填写账号',trigger: 'blur'}
        ],
        name:[
          {required: true ,message: '请填写名称',trigger: 'blur'}
        ]  ,
        phone:[
          {required: true ,message: '请填写电话',trigger: 'blur'}
        ] ,
        email:[
          {required: true ,message: '请填写邮箱',trigger: 'blur'}
        ]
      },
      rows:[],
      ids:[]
    }
)

const formRef =ref()

const load = () =>{
  request.get('/admin/selectPage',{
    params:{
      pageNum:data.pageNum,
      pageSize:data.pageSize,
      username:data.username,
      name:data.name

    }
  }).then(res =>{
    if(res.code === '200'){
      data.tableData=res.data.list
      data.total=res.data.total
    }else{
      ElMessage.error(res.msg)
    }
  })
}

load()

const reset = () => {
  data.username=null
  data.name=null
  load()
}

const handleAdd = () =>{
  data.formVisible=true
  data.form= {}
}

const add = () =>{
  // 表单验证
  formRef.value.validate((valid) =>{
    if(valid){
      request.post('/admin/add',data.form).then(res =>{
        if(res.code === '200'){

          ElMessage.success('新增成功')
          data.formVisible=false
          load()
        }else{
          ElMessage.error(res.msg)
        }
      })
    }
  })

}

const handleEdit = (row) =>{
  data.form=JSON.parse(JSON.stringify(row))
  data.formVisible=true
}

const update = () => {
  // 表单验证
  formRef.value.validate((valid) =>{
    if(valid){
      request.put('/admin/update',data.form).then(res =>{
        if(res.code === '200'){

          ElMessage.success('修改成功')
          data.formVisible=false
          load()
        }else{
          ElMessage.error(res.msg)
        }
      })
    }
  })
}

const save = () => {
  data.form.id ? update() : add()
}

const del = (id) =>{
  ElMessageBox.confirm('删除后无法恢复，您确认删除吗？','删除确认',{type :'warning'}).then(res =>{
    request.delete('/admin/delete/' + id).then(res =>{
      if(res.code === '200'){
        ElMessage.success('删除成功')
        load()
      }else{
        ElMessage.error(res.msg)
      }
    })
  }).catch(err =>{})
}

const handleSelectionChange = (rows) => {
  data.rows=rows
  data.ids=data.rows.map(v => v.id)
}

const deleteBatch = () =>{
  if(data.rows.length === 0){
    ElMessage.warning('请选择数据')
    return
  }
  ElMessageBox.confirm('删除后无法恢复，您确认删除吗？','删除确认',{type :'warning'}).then(res =>{
    request.delete('/admin/deleteBatch',{data: data.rows}).then(res =>{
      if(res.code === '200'){
        ElMessage.success('批量删除成功')
        load()
      }else{
        ElMessage.error(res.msg)
      }
    })
  }).catch(err =>{})
}

const exportData =()=>{
  let idsStr =data.ids.join(",")  //把数组转换为 字符串 [1,2,3] -> "1,2,3"
  let url =`/api/admin/export?username=${data.username === null ? '' : data.username}`
  + `&name=${data.name === null ? '' : data.name}`
  + `&ids=${idsStr}`
  + `&token=${data.user.token}`
  window.open(url)
}

const handleImportSuccess =(res) =>{
  if(res.code === '200'){
    ElMessage.success("导入成功")
    load()
  }else {
    ElMessage.error(res.msg)
  }

}

const handleFileSuccess = (res) =>{
  data.form.avatar=res.data

}

const handleDeleteAvatar = () => {
  data.form.avatar = ''       // 表单里清空

}

</script>

<style scoped>

/* 固定行高 */
.el-table .avatar-column .avatar-cell {
  width: 40px;
  height: 40px;        /* 固定高度 */
  display: flex;
  justify-content: center;
  align-items: center;  /* 图片居中 */
}

/* el-image 占满父容器 */
.el-table .avatar-column .avatar-cell >>> .el-image {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}


/* 固定上传组件容器 */
.avatar-uploader {
  width: 100px;
  height: 100px;
  display: inline-block;
  padding: 0 !important;
  margin: 0 !important;
  line-height: 0; /* 消除默认行高 */
}

.avatar-wrapper {
  position: relative;
  display: flex;             /* flex 布局 */
  justify-content: center;    /* 居中 */
  align-items: center;        /* 居中 */
  width: 100px;
  height: 100px;
  overflow: hidden;           /* 防止撑开 */
}

.avatar {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;          /* 裁剪填充 */

}

.delete-btn {
  position: absolute;
  top: 4px;
  right: 4px;
  cursor: pointer;
  color: white;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 50%;
  padding: 2px;
  opacity: 0;               /* 默认隐藏 */
  transition: opacity 0.2s; /* 平滑显示 */
  z-index: 10;  /* 确保在最上层 */
}

.avatar-wrapper:hover .delete-btn {
  opacity: 1;  /* 鼠标移入头像区域时显示 */
}

.avatar-uploader-icon {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 40px; /* 控制 Plus 图标大小 */
  color: #409EFF;  /* Element Plus 默认主题色 */
  border: 1px dashed #d9d9d9;
  border-radius: 50%;
  cursor: pointer;
}

</style>
