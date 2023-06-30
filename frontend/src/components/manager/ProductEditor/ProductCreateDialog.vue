<template>
  <v-dialog v-model="isCreateDialogOpen" width="400">
    <v-card>
      <v-card-text> Create Product </v-card-text>
      <v-card-item>
        <v-form @submit.prevent>
          <v-text-field v-model="name" label="name"></v-text-field>
          <v-text-field v-model="description" label="description"></v-text-field>
          <v-text-field v-model="price" type="number" label="price"></v-text-field>
          <v-text-field v-model="quantity" type="number" label="quantity"></v-text-field>
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
        <v-btn color="green" @click="createRequest">Create</v-btn>
        <v-btn color="grey" @click="closeDialog">Discard</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script lang="ts">
import { mapActions, mapState, mapWritableState } from 'pinia'
import { useCategoryEditorStore, type CategoryDTO } from '@/stores/categoryEditor'
import { useLoginStore } from '@/stores/login'
import { useProductEditorStore } from '@/stores/productEditor'

export default {
  data: () => ({
    categoryChosen: {
      name: '',
      description: ''
    } as CategoryDTO,
    name: '',
    description: '',
    quantity: 0,
    price: 0
  }),
  computed: {
    ...mapState(useCategoryEditorStore, ['categories']),
    ...mapState(useLoginStore, ['jwt_token']),
    ...mapWritableState(useProductEditorStore, ['isCreateDialogOpen'])
  },
  methods: {
    ...mapActions(useProductEditorStore, ['create']),
    async createRequest() {
      const formIsNotFilled = this.name.length == 0 || this.categoryChosen.name.length <= 0
      if (formIsNotFilled) return
      const isOk = await this.create(this.jwt_token, {
        name: this.name,
        description: this.description,
        category_name: this.categoryChosen.name,
        quantity: parseInt(this.quantity.toString()),
        price: this.price.toString()
      })
      if (isOk) this.closeDialog()
      else {
        alert('The error happened')
      }
    },
    closeDialog() {
      this.categoryChosen = {
        name: '',
        description: ''
      } as CategoryDTO
      this.description = ''
      this.name = ''
      this.quantity = 0
      this.price = 0
      this.isCreateDialogOpen = false
    }
  }
}
</script>
<style lang=""></style>
