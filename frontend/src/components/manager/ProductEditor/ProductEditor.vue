<template>
  <ProductCreateDialog />
  <ProductDeleteDialog />
  <ProductUpdateDialog />
  <TotalPriceDialog />
  <div class="editor-block v-container pa-5">
    <v-card class="mb-5">
      <v-card-title class="text-h4 font-weight-bold">Product</v-card-title>
      <v-card-action class="d-flex">
        <v-expansion-panels class="my-5">
          <v-expansion-panel title="Statistics">
            <v-expansion-panel-text class="advance-search-bar">
              <v-btn size="large" class="mb-5 mt-0 mx-5 bg-blue" @click="totalValueRequest"
                >Get total value<br />
                of the Warehouse</v-btn
              >
            </v-expansion-panel-text>
          </v-expansion-panel>
        </v-expansion-panels>
      </v-card-action>
      <v-card-actions class="d-flex">
        <v-text-field v-model="findByName" label="Find by name"></v-text-field>
        <v-btn size="large" class="mb-5 mt-0 mx-5 bg-blue" @click="findByNameRequest"
          >Find by name</v-btn
        >
      </v-card-actions>
      <v-card-actions class="d-flex">
        <v-select
          v-model="categoryFilter"
          density="compact"
          class="sort-by"
          :items="categoryFilterOptions"
          label="Category Filter"
        ></v-select>
        <v-btn @click="search" size="large" class="mb-5 mt-0 mx-5 bg-blue">Search</v-btn>
        <v-btn @click="create" size="large" class="mb-5 mt-0 bg-green">Create New</v-btn>
      </v-card-actions>
    </v-card>

    <v-table :height="tableHeight" hover fixed-header density="compact" class="data-table mt-5">
      <thead>
        <tr>
          <th>Name</th>
          <th>Category</th>
          <th>Description</th>
          <th>Quantity</th>
          <th>Price</th>
          <th>Options</th>
        </tr>
      </thead>
      <tbody v-if="products.length != 0">
        <tr v-for="(item, i) in products" :key="i">
          <td>{{ item.name }}</td>
          <td>{{ item.category_name }}</td>
          <td>{{ item.description }}</td>
          <td>{{ item.quantity }}</td>
          <td>{{ item.price }}</td>
          <td>
            <v-btn size="small" @click="updateRequest(item)" class="bg-orange">Update</v-btn>
            <v-btn size="small" @click="deleteRequest(item)" class="bg-red">Delete</v-btn>
          </td>
        </tr>
      </tbody>
    </v-table>
  </div>
</template>
<script lang="ts">
import { mapActions, mapState, mapWritableState } from 'pinia'
import { useProductEditorStore, type ProductDTO } from '@/stores/productEditor'
import { useLoginStore } from '@/stores/login'
import { useCategoryEditorStore } from '@/stores/categoryEditor'
import ProductCreateDialog from './ProductCreateDialog.vue'
import ProductDeleteDialog from './ProductDeleteDialog.vue'
import ProductUpdateDialog from './ProductUpdateDialog.vue'
import TotalPriceDialog from './TotalPriceDialog.vue'

export default {
  components: {
    ProductCreateDialog,
    ProductDeleteDialog,
    ProductUpdateDialog,
    TotalPriceDialog
  },
  mounted() {
    this.getAllCategories(this.jwt_token)
  },
  data() {
    return {
      categoryFilter: 'Any',
      findByName: ''
    }
  },
  computed: {
    ...mapWritableState(useProductEditorStore, [
      'isCreateDialogOpen',
      'chosenItem',
      'isDeleteDialogOpen',
      'isUpdateDialogOpen',
      'isTotalValueDialogOpen'
    ]),
    ...mapState(useProductEditorStore, ['products']),
    ...mapState(useLoginStore, ['jwt_token']),
    ...mapState(useCategoryEditorStore, ['categories']),
    tableHeight() {
      return window.innerHeight * 0.5
    },
    categoryFilterOptions() {
      return ['Any'].concat(this.categories.map((cat) => cat.name))
    }
  },
  methods: {
    ...mapActions(useProductEditorStore, ['getAll', 'getTotalWarehousePrice', 'getOneByName']),
    ...mapActions(useCategoryEditorStore, {
      getAllCategories: 'getAll'
    }),
    async search() {
      let isOk: boolean
      isOk = await this.getAll(this.jwt_token, this.categoryFilter)
      if (!isOk) alert('The error happened')
    },
    create() {
      this.isCreateDialogOpen = true
    },
    deleteRequest(item: ProductDTO) {
      this.chosenItem = item
      this.isDeleteDialogOpen = true
    },
    updateRequest(item: ProductDTO) {
      this.chosenItem = { ...item }
      this.isUpdateDialogOpen = true
    },
    async totalValueRequest() {
      await this.getTotalWarehousePrice(this.jwt_token, this.categoryFilter)
      this.isTotalValueDialogOpen = true
    },
    async findByNameRequest() {
      let isOk: boolean
      if (this.findByName.trim() != '')
        isOk = await this.getOneByName(this.jwt_token, this.findByName)
      else isOk = await this.getAll(this.jwt_token, this.categoryFilter)
      if (!isOk) alert('The error happened')
    }
  }
}
</script>
<style>
.editor-block {
  width: 100vw;
  height: 100vh;
  margin: 0;
  padding: 0;
}

.sort-by {
  max-width: 300px;
  margin-right: 20px;
}
</style>
