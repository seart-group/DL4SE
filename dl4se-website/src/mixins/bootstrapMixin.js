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
        },
        async showConfirmModal(title, message) {
            return this.$bvModal.msgBoxConfirm(
                message,
                {
                    title: title,
                    okTitle: "Yes",
                    okVariant: "danger",
                    cancelTitle: "No",
                    centered: true,
                    noStacking: true,
                    contentClass: "confirm-dialog-content",
                    headerClass: "confirm-dialog-header",
                    footerClass: "confirm-dialog-footer"
                }
            )
        }
    }
}