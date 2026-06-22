import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useMicroServiceStore = defineStore('microservice', () => {
    const prodcutsUrl = ref('http://localhost:8081')
    const inventoryUrl = ref('http://localhost:8082')
    return { prodcutsUrl, inventoryUrl }
})