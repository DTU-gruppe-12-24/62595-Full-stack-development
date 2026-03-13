import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

import './styles/base.css'
import './styles/theme.css'

// Setup Font Awesome icons (https://fontawesome.com/search?ic=free-collection)
import { library } from '@fortawesome/fontawesome-svg-core'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { faRightFromBracket, faPenToSquare, faTrash } from '@fortawesome/free-solid-svg-icons'
import { } from '@fortawesome/free-regular-svg-icons'
import { } from '@fortawesome/free-brands-svg-icons'
library.add([faRightFromBracket, faPenToSquare, faTrash])

createApp(App)
    .use(router)
    .component('font-awesome-icon', FontAwesomeIcon)
    .mount('#app')
