<template>
  <v-dialog v-model="isDeleteDialogOpen" width="400">
    <v-card>
      <v-card-text> Delete Category </v-card-text>
      <v-card-item>
        <p>Are you sure you want to delete this category?</p>
      </v-card-item>
      <v-card-actions>
        <v-btn color="red" @click="deleteRequest">Delete</v-btn>
        <v-btn color="grey" @click="closeDialog">Discard</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script lang="ts">
import { mapActions, mapState, mapWritableState } from 'pinia'
import { useCategoryEditorStore, type CategoryDTO } from '@/stores/categoryEditor'
import { useLoginStore } from '@/stores/login'

export default {
  data: () => ({}),
  computed: {
    ...mapWritableState(useCategoryEditorStore, ['isDeleteDialogOpen', 'chosenItem']),
    ...mapState(useLoginStore, ['jwt_token'])
  },
  methods: {
    ...mapActions(useCategoryEditorStore, ['delete']),
    async deleteRequest() {
      const isOk = await this.delete(this.jwt_token, this.chosenItem.name)
      if (isOk) this.closeDialog()
      else {
        alert('The error happened')
      }
    },
    closeDialog() {
      this.chosenItem = {} as CategoryDTO
      this.isDeleteDialogOpen = false
    }
  }
}
</script>
<style lang=""></style>
