<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <div class="app-layout">
    <!-- Premium Top Navigation Bar -->
    <header class="app-header">
      <div class="nav-container">
        <!-- Logo Area -->
        <div class="brand">
          <div class="logo-icon">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"/>
            </svg>
          </div>
          <span class="brand-name">Linktic <span class="accent-text">Sistemas</span></span>
        </div>

        <!-- Navigation Links -->
        <nav class="navigation">
          <RouterLink to="/" class="nav-link" exact-active-class="active">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="link-icon">
              <path d="M12 5v14M5 12h14"/>
            </svg>
            <span>Registrar Producto</span>
          </RouterLink>
          
          <RouterLink to="/inventario" class="nav-link" exact-active-class="active">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="link-icon">
              <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/>
            </svg>
            <span>Ver Inventario</span>
          </RouterLink>
        </nav>
      </div>
    </header>

    <!-- Main View Content -->
    <main class="main-content">
      <RouterView />
    </main>
  </div>
</template>

<style>
/* Global App/Reset Adjustments */
#app {
  max-width: 100% !important;
  width: 100% !important;
  margin: 0 !important;
  padding: 0 !important;
  display: block !important;
}

body {
  margin: 0;
  padding: 0;
  display: block !important;
}
</style>

<style scoped lang="scss">
.app-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: var(--color-background);
}

.app-header {
  position: sticky;
  top: 0;
  z-index: 1000;
  background-color: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--color-border);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.02);
  transition: all 0.3s ease;

  @media (prefers-color-scheme: dark) {
    background-color: rgba(24, 24, 24, 0.85);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  }
}

.nav-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
  padding: 16px 24px;
  flex-wrap: wrap;
  gap: 16px;

  @media (max-width: 600px) {
    flex-direction: column;
    align-items: center;
    padding: 12px;
  }
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;

  .logo-icon {
    width: 36px;
    height: 36px;
    background: linear-gradient(135deg, #41b883, #35495e);
    color: white;
    border-radius: 8px;
    display: flex;
    justify-content: center;
    align-items: center;
    box-shadow: 0 4px 10px rgba(65, 184, 131, 0.3);

    svg {
      width: 20px;
      height: 20px;
    }
  }

  .brand-name {
    font-size: 1.25rem;
    font-weight: 800;
    color: var(--color-heading);
    letter-spacing: -0.5px;
  }

  .accent-text {
    color: #41b883;
    font-weight: 700;
  }
}

.navigation {
  display: flex;
  align-items: center;
  gap: 8px;

  @media (max-width: 600px) {
    width: 100%;
    justify-content: center;
  }
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 18px;
  color: var(--color-text);
  opacity: 0.8;
  font-size: 0.92rem;
  font-weight: 600;
  border-radius: 8px;
  text-decoration: none;
  background-color: transparent;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);

  &:hover {
    color: #41b883;
    opacity: 1;
    background-color: rgba(65, 184, 131, 0.06) !important;
  }

  .link-icon {
    width: 16px;
    height: 16px;
    transition: transform 0.2s ease;
  }

  &:hover .link-icon {
    transform: scale(1.1);
  }

  &.active {
    color: #ffffff !important;
    opacity: 1;
    background: linear-gradient(135deg, #41b883, #35495e);
    box-shadow: 0 4px 12px rgba(65, 184, 131, 0.25);

    .link-icon {
      color: #ffffff;
    }

    &:hover {
      background: linear-gradient(135deg, #46c88e, #3d556e);
    }
  }
}

.main-content {
  flex: 1;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  padding: 24px;
}
</style>
