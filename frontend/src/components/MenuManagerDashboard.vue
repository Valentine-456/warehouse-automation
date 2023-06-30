<template>
  <v-navigation-drawer class="bg-grey-lighten-2">
    <v-list density="compact" nav>
      <v-list-item
        v-for="option in menuOptions"
        :key="option[1]"
        :title="option[0]"
        :value="option[1]"
        @click="$router.push(option[1])"
      ></v-list-item>
      <v-list-item :title="'Logout'" @click="logout" class="bg-red"></v-list-item>
    </v-list>
  </v-navigation-drawer>

  <v-main style="height: 250px"></v-main>
</template>
<script lang="ts">
import { useLoginStore } from '@/stores/login'
import { mapActions } from 'pinia'

export default {
  data: () => ({
    menuOptions: [
      ['Category', '/dashboard/category'],
      ['Product', '/dashboard/product']
    ]
  }),
  methods: {
    ...mapActions(useLoginStore, ['clearStore']),
    logout() {
      this.clearStore()
      this.$router.push('/')
    }
  }
}
</script>

<style scoped></style>
