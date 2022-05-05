export default {
    methods: {
        appendToast(title, message, variant) {
            this.$bvToast.toast(message, {
                title: title,
                variant: variant,
                toaster: "b-toaster-top-right",
                autoHideDelay: 4500,
                appendToast: true
            })
        }
    }
}