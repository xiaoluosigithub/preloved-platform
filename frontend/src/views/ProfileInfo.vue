<template>
  <div class="profile-info-container">
    <el-card class="profile-info-card">
      <div class="card-header">
        <div class="header-icon">
          <el-icon size="24"><User /></el-icon>
        </div>
        <h2 class="card-title">个人资料</h2>
        <p class="card-subtitle">管理您的个人信息和账户设置</p>
      </div>
      
      <el-form 
        :model="form" 
        label-position="top" 
        class="profile-form"
        @submit.prevent="save">
        
        <!-- Avatar Section -->
        <el-form-item label="头像" class="avatar-form-item">
          <div class="avatar-upload-section">
            <div class="avatar-preview">
              <img 
                v-if="form.avatar" 
                :src="form.avatar" 
                class="avatar-image"
                alt="用户头像"
              />
              <div v-else class="avatar-placeholder">
                <el-icon size="32"><User /></el-icon>
              </div>
            </div>
            <div class="avatar-actions">
              <el-upload 
                :action="'/api/upload/image'" 
                :on-success="onAvatarUpload" 
                :show-file-list="false" 
                name="file"
                class="avatar-upload">
                <el-button type="primary" :icon="Camera" size="default">
                  上传头像
                </el-button>
              </el-upload>
              <el-button 
                v-if="form.avatar" 
                link 
                @click="removeAvatar"
                class="remove-avatar-btn">
                移除头像
              </el-button>
            </div>
          </div>
        </el-form-item>

        <!-- Basic Info Section -->
        <div class="form-section">
          <h3 class="section-title">基本信息</h3>
          
          <el-form-item label="昵称" prop="nickname">
            <el-input 
              v-model="form.nickname" 
              placeholder="请输入昵称"
              size="large"
              :prefix-icon="User">
            </el-input>
          </el-form-item>
          
          <el-form-item label="用户名" prop="username">
            <el-input 
              v-model="form.username" 
              disabled
              size="large"
              :prefix-icon="Avatar">
            </el-input>
          </el-form-item>
          
          <el-form-item label="个性签名" prop="signature">
            <el-input 
              v-model="form.signature" 
              type="textarea"
              :rows="3"
              placeholder="介绍一下自己吧..."
              maxlength="100"
              show-word-limit>
            </el-input>
          </el-form-item>
        </div>

        <!-- Action Buttons -->
        <el-form-item class="form-actions">
          <el-button 
            type="primary" 
            @click="save"
            size="large"
            :loading="loading"
            class="save-btn">
            <el-icon><Check /></el-icon>
            保存修改
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { 
  User, 
  Avatar, 
  Camera, 
  Check 
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import api from '@/api'

export default {
  name: 'ProfileInfo',
  components: {
    User,
    Avatar,
    Camera,
    Check
  },
  setup() {
    const form = ref({
      username: '',
      nickname: '',
      avatar: '',
      signature: ''
    })
    const loading = ref(false)

    const loadUserInfo = async () => {
      try {
        const res = await api.get('/user/me')
        const user = res.data.data || {}
        form.value = {
          username: user.username || '',
          nickname: user.nickname || '',
          avatar: user.avatar || '',
          signature: user.signature || ''
        }
      } catch (err) {
        console.error('Failed to load user info:', err)
        ElMessage.error('加载用户信息失败')
      }
    }

    const onAvatarUpload = (res) => {
      const url = res?.data?.data || res?.data || res
      if (url) {
        form.value.avatar = url
        ElMessage.success('头像上传成功')
      }
    }

    const removeAvatar = () => {
      form.value.avatar = ''
      ElMessage.success('头像已移除')
    }

    const save = async () => {
      if (!form.value.nickname?.trim()) {
        ElMessage.warning('请输入昵称')
        return
      }

      loading.value = true
      try {
        await api.post('/user/update', {
          nickname: form.value.nickname,
          avatar: form.value.avatar,
          signature: form.value.signature
        })
        
        // Refresh user data
        await loadUserInfo()
        
        // Update local storage
        const res = await api.get('/user/me')
        const u = res.data.data || {}
        localStorage.setItem('user', JSON.stringify(u))
        window.dispatchEvent(new Event('auth-changed'))
        
        ElMessage.success('个人资料保存成功')
      } catch (err) {
        const msg = err.response?.data?.msg || '保存失败'
        ElMessage.error(msg)
      } finally {
        loading.value = false
      }
    }


    onMounted(() => {
      loadUserInfo()
    })

    return {
      form,
      loading,
      onAvatarUpload,
      removeAvatar,
      save,
      User,
      Avatar,
      Camera,
      Check
    }
  }
}
</script>

<style scoped>
.profile-info-container {
  max-width: 800px;
  margin: 0 auto;
}

.profile-info-card {
  border-radius: 16px;
  box-shadow: var(--shadow-md);
  border: none;
  padding: 32px;
}

.card-header {
  text-align: center;
  margin-bottom: 32px;
}

.header-icon {
  width: 60px;
  height: 60px;
  background: var(--primary-gradient);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  color: white;
  box-shadow: var(--shadow-md);
}

.card-title {
  font-size: 1.8rem;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 8px 0;
}

.card-subtitle {
  color: var(--text-secondary);
  font-size: 1rem;
  margin: 0;
}

.profile-form {
  max-width: 600px;
  margin: 0 auto;
}

.form-section {
  margin-bottom: 32px;
}

.section-title {
  font-size: 1.2rem;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid var(--border-color);
}

.avatar-form-item {
  margin-bottom: 32px;
}

.avatar-upload-section {
  display: flex;
  align-items: center;
  gap: 24px;
}

.avatar-preview {
  position: relative;
}

.avatar-image {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid var(--primary-light);
  box-shadow: var(--shadow-md);
}

.avatar-placeholder {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: var(--primary-gradient);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 2rem;
  box-shadow: var(--shadow-md);
}

.avatar-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.remove-avatar-btn {
  color: var(--danger-color);
  padding: 4px 8px;
}

.form-actions {
  margin-top: 32px;
  text-align: center;
}

.save-btn {
  font-weight: 600;
}

/* Responsive Design */
@media (max-width: 768px) {
  .profile-info-card {
    padding: 24px;
  }
  
  .avatar-upload-section {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .form-actions {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }
  
  .save-btn {
    margin-right: 0;
  }
}

@media (max-width: 480px) {
  .card-title {
    font-size: 1.5rem;
  }
  
  .avatar-image,
  .avatar-placeholder {
    width: 80px;
    height: 80px;
  }
}
</style>