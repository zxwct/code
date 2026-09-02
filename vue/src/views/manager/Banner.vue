<template>
  <div class="card" style="margin-bottom: 5px">
    <el-input clearable @clear="load" style="width: 300px;margin-right: 5px" v-model="data.name"
              placeholder="请输入说明查询" :prefix-icon="Search"></el-input>
    <el-button type="primary" @click="load">查 询</el-button>
    <el-button type="info" @click="reset">重 置</el-button>
  </div>

  <div class="card" style="margin-bottom: 5px" v-if="data.user.role === 'ADMIN'">
    <el-button type="primary" @click="handleAdd">新增</el-button>
  </div>

  <div class="card" style="margin-bottom: 5px">
    <el-table :data="data.tableData" style="width: 100%"
              :header-cell-style="{color: '#333',backgroundColor:'#8a9ef1'}">

      <el-table-column prop="name" label="轮播图说明"/>
      <el-table-column label="轮播图图片" width="500px" class-name="img-column" label-class-name="img-column-header">
        <template #default="scope">
          <div class="img-cell">
            <el-image v-if="scope.row.img" :src="scope.row.img" :preview-src-list="[scope.row.img]" :preview-teleported="true"
                      style="width: 200px;height: 100px; border-radius: 5px; display: block"/>
          </div>
        </template>
      </el-table-column>


      <el-table-column label="操作" width="200px">
        <template #default="scope" v-if="data.user.role === 'ADMIN'">
          <el-button type="primary" icon="Edit" circle @click="handleEdit(scope.row)"></el-button>
          <el-button type="danger" icon="Delete" circle @click="del(scope.row.id)"></el-button>
        </template>
        <template #default="scope" v-else>
          <el-button type="primary" @click="borrow(scope.row)">借阅</el-button>
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

  <el-dialog v-model="data.formVisible" name="轮播图管理" width="30%" destroy-on-close>

    <el-form ref="formRef" :model="data.form" :rules="data.rules" label-width="80px" style="padding: 20px 30px 10px 0">
      <el-form-item prop="img" label="轮播图">
        <el-upload
            action="http://localhost:9999/files/upload"
            :show-file-list="false"
            :headers="{token: data.user.token}"
            :on-success="handleFileSuccess"
            list-type="picture"
            class="img-uploader"
        >
          <template #default>
            <div class="img-wrapper">
              <!-- 显示头像 -->
              <img
                  v-if="data.form.img"
                  :src="data.form.img"
                  class="img"
              />

              <!-- 删除按钮（默认隐藏，hover 显示） -->
              <el-icon
                  v-if="data.form.img"
                  @click.stop="handleDeleteAvatar"
                  class="delete-btn"
              >
                <Close />
              </el-icon>
              <!-- 如果没有头像，就显示上传按钮 -->
              <el-icon
                  v-else
                  class="img-uploader-icon"
              >
                <Plus />
              </el-icon>
            </div>
          </template>
        </el-upload>
      </el-form-item>
      <el-form-item prop="name" label="图片说明">
        <el-input v-model="data.form.name" autocomplete="off" placeholder="请输入图书说明"/>
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

</template>


<script setup>
import {Search} from "@element-plus/icons-vue";
import {reactive,ref} from "vue";
import request from "@/utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";

const formRef = ref()

const data = reactive({
      user: JSON.parse(localStorage.getItem('code_user') || '{}'),
      name: null,
      pageNum: 1,
      pageSize: 6,
      total: 0,
      tableData: [],
      form: {},
      formVisible: false,
      rules: {
        img: [
          {required: true, message: '请上传轮播图', trigger: 'blur'}
        ],
        name: [
          {required: true, message: '请填写图片说明', trigger: 'blur'}
        ],
     

      },


    }
)

const load = () => {
  request.get('/banner/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      name: data.name,
    }
  }).then(res => {
    if (res.code === '200') {
      data.tableData = res.data.list
      data.total = res.data.total
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const reset = () =>{
  data.name=null
  load()
}

load()

const handleAdd = () => {
  data.form = {}
  data.formVisible = true
}

const handleEdit = (row) =>{
  data.form=JSON.parse(JSON.stringify(row))
  data.formVisible=true
}

const add = () =>{
  // 表单验证
  formRef.value.validate((valid) =>{
    if(valid){
      request.post('/banner/add',data.form).then(res =>{
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

const update = () => {
  // 表单验证
  formRef.value.validate((valid) =>{
    if(valid){
      request.put('/banner/update',data.form).then(res =>{
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

const save = () =>{
  data.form.id ? update() : add()
}

const del = (id) => {
  ElMessageBox.confirm('删除后无法恢复，您确认删除吗？','删除确认',{type :'warning'}).then(res =>{
    request.delete('/banner/delete/' + id).then(res =>{
      if(res.code === '200'){
        ElMessage.success('删除成功')
        load()
      }else{
        ElMessage.error(res.msg)
      }
    })
  }).catch(err =>{})
}

const handleFileSuccess = (res) =>{
  data.form.img=res.data

}

const handleDeleteAvatar = () => {
  data.form.img = ''       // 表单里清空

}


</script>

<style scoped>



/* 固定上传组件容器 */
.img-uploader {
  width: 100px;
  height: 100px;
  display: inline-block;
  padding: 0 !important;
  margin: 0 !important;
  line-height: 0; /* 消除默认行高 */
}

.img-wrapper {
  position: relative;
  display: flex;             /* flex 布局 */
  justify-content: center;    /* 居中 */
  align-items: center;        /* 居中 */
  width: 200px;
  height: 100px;
  overflow: hidden;           /* 防止撑开 */
}

.img {
  width: 100%;
  height: 100%;
  border-radius: 5px;
  object-fit: cover;          /* 裁剪填充 */

}

.delete-btn {
  position: absolute;
  top: 4px;
  right: 4px;
  cursor: pointer;
  color: white;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 5px;
  padding: 2px;
  opacity: 0;               /* 默认隐藏 */
  transition: opacity 0.2s; /* 平滑显示 */
  z-index: 10;  /* 确保在最上层 */
}

.img-wrapper:hover .delete-btn {
  opacity: 1;  /* 鼠标移入头像区域时显示 */
}

.img-uploader-icon {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 40px; /* 控制 Plus 图标大小 */
  color: #409EFF;  /* Element Plus 默认主题色 */
  border: 1px dashed #d9d9d9;
  border-radius: 5px;
  cursor: pointer;
}

</style>
