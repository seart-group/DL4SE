import {words} from 'lodash'

export default {
    methods: {
        appendToast(title, message, variant) {
            this.$bvToast.toast(message, {
                toaster: "b-toaster-top-right",
                title: title,
                variant: variant,
                solid: true,
                autoHideDelay: (words(`${title} ${message}`).length / 5) * 1000 + 1000,
                appendToast: true
            })
        }
    }
}