/* eslint-disable @typescript-eslint/no-unused-vars */
import { defineStore } from 'pinia'
export interface CategoryDTO {
  name: string
  description: string
}

export interface DataCategoryDTO {
  '@items': Array<CategoryDTO>
}

export const useCategoryEditorStore = defineStore('categoryEditorStore', {
  state: () => ({
    isCreateDialogOpen: false,
    isDeleteDialogOpen: false,
    isUpdateDialogOpen: false,
    chosenItem: {} as CategoryDTO,
    categories: [] as Array<CategoryDTO>
  }),
  getters: {},
  actions: {
    async getAll(jwt_token: string): Promise<boolean> {
      const url = 'http://127.0.0.1:8080/api/category'
      console.log(url)
      try {
        const response = await fetch(url, {
          headers: {
            Authorization: jwt_token
          }
        })
        console.log()
        if (response.ok) {
          const data: DataCategoryDTO = await response.json()
          this.categories = data['@items']
          return true
        }
        return false
      } catch (error) {
        console.error('Error:', error)
        return false
      }
    },
    async create(jwt_token: string, category: CategoryDTO): Promise<boolean> {
      const url = 'http://127.0.0.1:8080/api/category'
      try {
        const response = await fetch(url, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            Authorization: jwt_token
          },
          body: JSON.stringify(category)
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
    async delete(jwt_token: string, name: String): Promise<boolean> {
      const url = `http://127.0.0.1:8080/api/category/${name}`
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
    async update(jwt_token: string, category: CategoryDTO): Promise<boolean> {
      const url = `http://127.0.0.1:8080/api/category/${category.name}`
      try {
        const response = await fetch(url, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: jwt_token
          },
          body: JSON.stringify(category)
        })
        if (response.ok) {
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
