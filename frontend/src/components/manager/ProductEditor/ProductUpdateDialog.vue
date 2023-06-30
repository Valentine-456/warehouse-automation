<template>
  <v-dialog v-model="isUpdateDialogOpen" width="400">
    <v-card>
      <v-card-text> Update Product </v-card-text>
      <v-card-item>
        <v-form @submit.prevent>
          <v-text-field v-model="chosenItem.name" :readonly="true" label="name"></v-text-field>
          <v-text-field v-model="chosenItem.description" label="description"></v-text-field>
          <v-text-field v-model="chosenItem.price" type="number" label="price"></v-text-field>
          <v-text-field v-model="chosenItem.quantity" type="number" label="quantity"></v-text-field>
          <v-select
            v-model="categoryChosen"
            :items="categories"
            item-title="name"
            item-value="name"
            label="Category"
            persistent-hint
            return-object
            single-line
          ></v-select>
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
import { useProductEditorStore, type ProductDTO } from '@/stores/productEditor'

export default {
  data: () => ({
    categoryChosen: {
      name: '',
      description: ''
    } as CategoryDTO
  }),
  computed: {
    ...mapWritableState(useProductEditorStore, ['isUpdateDialogOpen', 'chosenItem']),
    ...mapState(useLoginStore, ['jwt_token']),
    ...mapState(useCategoryEditorStore, ['categories'])
  },
  methods: {
    ...mapActions(useProductEditorStore, ['update']),
    async updateRequest() {
      let formIsNotCompleted = this.chosenItem.category_name.length == 0
      if (formIsNotCompleted) return
      const isOk = await this.update(this.jwt_token, {
        ...this.chosenItem,
        quantity: parseInt(this.chosenItem.quantity.toString())
      })
      if (isOk) this.closeDialog()
      else {
        alert('The error happened')
      }
    },
    closeDialog() {
      this.chosenItem = {} as ProductDTO
      this.isUpdateDialogOpen = false
    }
  }
}
</script>
<style lang=""></style>
