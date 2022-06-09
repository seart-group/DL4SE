export default {
    methods: {
        appendToast(title, message, variant) {
            this.$bvToast.toast(message, {
                toaster: "b-toaster-top-right",
                title: title,
                variant: variant,
                solid: true,
                autoHideDelay: 4500,
                appendToast: true
            })
        }
    }
}