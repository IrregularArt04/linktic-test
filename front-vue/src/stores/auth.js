import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', () => {
  const apiKey = ref('IJSKLs548dojklsfuxna884suzxHSU')

  return { apiKey }
})