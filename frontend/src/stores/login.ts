/* eslint-disable @typescript-eslint/no-unused-vars */
import { defineStore } from 'pinia'
export interface loginDTO {
  id_employee: string
  password: string
}

export const useLoginStore = defineStore('loginStore', {
  state: () => ({
    jwt_token: ''
  }),
  getters: {},
  actions: {
    async loginRequest({ id_employee, password }: loginDTO): Promise<boolean> {
      const url = `http://localhost:8080/login?login=${id_employee}&password=${password}`

      try {
        const response = await fetch(url, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          }
        })

        if (response.ok) {
          const body = await response.text()
          this.jwt_token = body
          console.log(response.headers)
          return true
        }
        return false
      } catch (error) {
        console.error('Error:', error)
        return false
      }
    },
    clearStore() {
      this.jwt_token = ''
    }
  }
})
