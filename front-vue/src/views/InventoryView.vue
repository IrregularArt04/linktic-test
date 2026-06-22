<template>
  <main class="inventory-view-main">
    <InventoryTable />
  </main>
</template>

<script setup>
import InventoryTable from '../components/InventoryTable.vue'
</script>

<style scoped>
.inventory-view-main {
  width: 100%;
  min-height: 85vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  padding-top: 20px;
}
</style>
