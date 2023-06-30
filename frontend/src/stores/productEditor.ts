/* eslint-disable @typescript-eslint/no-unused-vars */
import { defineStore } from 'pinia'
export interface ProductDTO {
  quantity: number
  category_name: string
  name: string
  description: string
  price: string
}

export interface DataProductDTO {
  '@items': Array<ProductDTO>
}

export const useProductEditorStore = defineStore('productEditorStore', {
  state: () => ({
    isCreateDialogOpen: false,
    isDeleteDialogOpen: false,
    isUpdateDialogOpen: false,
    isTotalValueDialogOpen: false,
    chosenItem: {} as ProductDTO,
    products: [] as Array<ProductDTO>,
    totalValue: ''
  }),
  getters: {},
  actions: {
    async getAll(jwt_token: string, categoryFilter: string = 'Any'): Promise<boolean> {
      let url = 'http://127.0.0.1:8080/api/good'
      if (categoryFilter != 'Any') {
        url += `?category=${encodeURIComponent(categoryFilter)}`
      }
      console.log(url)
      try {
        const response = await fetch(url, {
          headers: {
            Authorization: jwt_token
          }
        })
        if (response.ok) {
          const data: DataProductDTO = await response.json()
          if(data['@items'] == null || data['@items'].length == 0)
            this.products = []
          else 
            this.products = data['@items'].map(
            (item) =>
              ({
                price: item.price,
                description: item.description,
                name: item.name,
                category_name: item.category_name,
                quantity: item.quantity
              } as ProductDTO)
          )
          return true
        }
        return false
      } catch (error) {
        console.error('Error:', error)
        return false
      }
    },
    async create(jwt_token: string, product: ProductDTO): Promise<boolean> {
      const url = 'http://127.0.0.1:8080/api/good'
      try {
        console.log(JSON.stringify(product))
        const response = await fetch(url, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            Authorization: jwt_token
          },
          body: JSON.stringify(product)
        })
        if (response.ok) {
          return true
        }
        return false
      } catch (error) {
        console.error('Error:', error)
        return false
      }
    },
    async delete(jwt_token: string, name: string): Promise<boolean> {
      const url = `http://127.0.0.1:8080/api/good/${name}`
      console.log(url)
      try {
        const response = await fetch(url, {
          method: 'DELETE',
          headers: {
            Authorization: jwt_token
          }
        })
        if (response.ok) {
          return true
        }
        return false
      } catch (error) {
        console.error('Error:', error)
        return false
      }
    },
    async update(jwt_token: string, product: ProductDTO): Promise<boolean> {
      const url = `http://127.0.0.1:8080/api/good/${product.name}`
      console.log(url)
      console.log(JSON.stringify(product))
      try {
        const response = await fetch(url, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: jwt_token
          },
          body: JSON.stringify(product)
        })
        if (response.ok) {
          return true
        }
        return false
      } catch (error) {
        console.error('Error:', error)
        return false
      }
    },
    async getTotalWarehousePrice(
      jwt_token: string,
      categoryFilter: string = 'Any'
    ): Promise<boolean> {
      let url = 'http://127.0.0.1:8080/api/statistics/totalPriceOfStore'
      if (categoryFilter != 'Any') {
        url += `?category=${encodeURIComponent(categoryFilter)}`
      }
      console.log(url)
      try {
        const response = await fetch(url, {
          headers: {
            Authorization: jwt_token
          }
        })
        if (response.ok) {
          const data: string = await response.text()
          this.totalValue = data
          return true
        }
        return false
      } catch (error) {
        console.error('Error:', error)
        return false
      }
    },
    async getOneByName(jwt_token: string, name: string): Promise<boolean> {
      const url = `http://127.0.0.1:8080/api/good/${name}`
      console.log(url)
      try {
        const response = await fetch(url, {
          headers: {
            Authorization: jwt_token
          }
        })
        if (response.ok) {
          const data: ProductDTO = await response.json()
          this.products = [
            {
              price: data.price,
              description: data.description,
              name: data.name,
              category_name: data.category_name,
              quantity: data.quantity
            }
          ]
          return true
        }
        return false
      } catch (error) {
        console.error('Error:', error)
        return false
      }
    }
  }
})
