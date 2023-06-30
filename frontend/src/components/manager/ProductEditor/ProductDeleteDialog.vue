<template>
  <v-dialog v-model="isDeleteDialogOpen" width="400">
    <v-card>
      <v-card-text> Delete Product </v-card-text>
      <v-card-item>
        <p>Are you sure you want to delete this product?</p>
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
import { useProductEditorStore, type ProductDTO } from '@/stores/productEditor'
import { useLoginStore } from '@/stores/login'

export default {
  data: () => ({}),
  computed: {
    ...mapWritableState(useProductEditorStore, ['isDeleteDialogOpen', 'chosenItem']),
    ...mapState(useLoginStore, ['jwt_token'])
  },
  methods: {
    ...mapActions(useProductEditorStore, ['delete']),
    async deleteRequest() {
      const isOk = await this.delete(this.jwt_token, this.chosenItem.name)
      if (isOk) this.closeDialog()
      else {
        alert('The error happened')
      }
    },
    closeDialog() {
      this.chosenItem = {} as ProductDTO
      this.isDeleteDialogOpen = false
    }
  }
}
</script>
<style lang=""></style>
