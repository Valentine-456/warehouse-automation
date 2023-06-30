<template>
  <v-dialog v-model="isTotalValueDialogOpen" width="400">
    <v-card>
      <v-card-text> Total value:</v-card-text>
      <v-card-item>
        <p>{{ totalValue }}</p>
      </v-card-item>
      <v-card-actions>
        <v-btn color="grey" @click="closeDialog">Close</v-btn>
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
    ...mapWritableState(useProductEditorStore, ['isTotalValueDialogOpen', 'totalValue']),
    ...mapState(useLoginStore, ['jwt_token'])
  },
  methods: {
    closeDialog() {
      this.totalValue = ''
      this.isTotalValueDialogOpen = false
    }
  }
}
</script>
<style lang=""></style>
