<script lang="ts">
import LoginForm from '../components/LoginForm.vue'
import { RouterLink } from 'vue-router'

export default {
  data() {
    return {
      count: 0
    }
  },
  components: {
    LoginForm
  }
}
</script>

<template>
  <main class="d-flex bg-surface-variant">
    <div class="sheet-block bg-grey-lighten-2">
      <v-card class="greeting-text bg-grey-lighten-2 elevation-0" width="380">
        <v-card-item>
          <v-card-title class="text-h4 font-weight-bold pa-5">The Warehouse</v-card-title>
        </v-card-item>
        <v-divider :thickness="2"></v-divider>

        <v-card-text class="text-subtitle-1">
          Welcome to the Warehouse admin panel. <br />
          It is designed to assist in managing goods in the Warehouse. <br />
          To login, please eneter your ID and password.
        </v-card-text>
      </v-card>
    </div>
    <div class="sheet-block bg-green-darken-1">
      <LoginForm class="login-form" message="Login" />
    </div>
  </main>
</template>

<style scoped>
main {
  width: 100vw;
  height: 100vh;
  position: relative;
  box-sizing: border-box;
  padding: 0;
  margin: 0;
  justify-content: space-between;
  flex-grow: 1;
}

.greeting-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-20%, -70%);
}

.sheet-block {
  width: 50vw;
  height: 100vh;
  position: relative;
  box-sizing: border-box;
  padding: 0;
  margin: 0;
  flex-grow: 1;
}

.login-form {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-70%, -50%);
}
</style>
