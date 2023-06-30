<template>
  <v-dialog v-model="isUpdateDialogOpen" width="400">
    <v-card>
      <v-card-text> Update Category </v-card-text>
      <v-card-item>
        <v-form @submit.prevent>
          <v-text-field
            v-model="chosenItem.name"
            :readonly="true"
            label="category_name"
          ></v-text-field>
          <v-text-field v-model="chosenItem.description" label="description"></v-text-field>
        </v-form>
      </v-card-item>
      <v-card-actions>
        <v-btn color="orange" @click="updateRequest">Update</v-btn>
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
    ...mapWritableState(useCategoryEditorStore, ['isUpdateDialogOpen', 'chosenItem']),
    ...mapState(useLoginStore, ['jwt_token'])
  },
  methods: {
    ...mapActions(useCategoryEditorStore, ['update']),
    async updateRequest() {
      let formIsNotCompleted = this.chosenItem.name.length == 0
      if (formIsNotCompleted) return
      const isOk = await this.update(this.jwt_token, this.chosenItem)
      if (isOk) this.closeDialog()
      else {
        alert('The error happened')
      }
    },
    closeDialog() {
      this.chosenItem = {} as CategoryDTO
      this.isUpdateDialogOpen = false
    }
  }
}
</script>
<style lang=""></style>
