<script lang="ts">
import { useLoginStore } from '@/stores/login'
import { mapActions } from 'pinia'

export default {
  data() {
    return {
      id_employee: '',
      password: '',
      notificationMessage: '',
      snackbar: false,
      rules: [
        (value: any) => {
          if (value) return true
          return 'Provide input here'
        }
      ]
    }
  },
  computed: {
    currentRoute() {
      return this.$route.path
    }
  },
  props: {
    message: String
  },
  methods: {
    ...mapActions(useLoginStore, ['loginRequest']),
    async alertForm() {
      if (this.id_employee == '' || this.password == '') return
      const result = await this.loginRequest({
        id_employee: this.id_employee,
        password: this.password
      })
      if (result) {
        this.notificationMessage = 'Successfully loged in!)'
        this.snackbar = true
        setTimeout(() => this.redirectToDashboard(), 2000)
      } else {
        this.notificationMessage = "Didn't log in :("
        this.snackbar = true
      }
      this.id_employee = ''
      this.password = ''
    },
    redirectToDashboard() {
      this.$router.push('/dashboard')
    }
  }
}
</script>

<template>
  <v-card class="login-form pb-5 pt-5" width="400">
    <v-card-item>
      <v-card-title class="text-center">{{ message }}</v-card-title>
    </v-card-item>
    <v-card-item>
      <v-form @submit.prevent>
        <v-text-field v-model="id_employee" label="Employee ID" :rules="rules"></v-text-field>
        <v-text-field v-model="password" label="Password" :rules="rules"></v-text-field>
        <v-btn type="submit" @click="alertForm" block class="mt-2">Submit</v-btn>
      </v-form>
    </v-card-item>

    <v-snackbar v-model="snackbar" :timeout="2000">
      {{ notificationMessage }}
      <template v-slot:actions>
        <v-btn color="grey" variant="text" @click="snackbar = false"> Close </v-btn>
      </template>
    </v-snackbar>
  </v-card>
</template>

<style scoped></style>
