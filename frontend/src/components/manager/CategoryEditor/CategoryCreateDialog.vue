<template>
  <v-dialog v-model="isCreateDialogOpen" width="400">
    <v-card>
      <v-card-text> Create Category </v-card-text>
      <v-card-item>
        <v-form @submit.prevent>
          <v-text-field v-model="name" label="category_name"></v-text-field>
          <v-text-field v-model="description" label="description"></v-text-field>
        </v-form>
      </v-card-item>
      <v-card-actions>
        <v-btn color="green" @click="createRequest">Create</v-btn>
        <v-btn color="grey" @click="closeDialog">Discard</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script lang="ts">
import { mapActions, mapState, mapWritableState } from 'pinia'
import { useCategoryEditorStore } from '@/stores/categoryEditor'
import { useLoginStore } from '@/stores/login'

export default {
  data: () => ({
    name: '',
    description: ''
  }),
  computed: {
    ...mapWritableState(useCategoryEditorStore, ['isCreateDialogOpen']),
    ...mapState(useLoginStore, ['jwt_token'])
  },
  methods: {
    ...mapActions(useCategoryEditorStore, ['create']),
    async createRequest() {
      if (this.name.length == 0) return
      const isOk = await this.create(this.jwt_token, {
        name: this.name,
        description: this.description
      })
      if (isOk) this.closeDialog()
      else {
        alert('The error happened')
      }
    },
    closeDialog() {
      this.name = ''
      this.description = ''
      this.isCreateDialogOpen = false
    }
  }
}
</script>
<style lang=""></style>
