export default {
    methods: {
        format(value, decimals = 2, k = 1000, units = ["", "K", "M", "B", "T"]) {
            if (!value) return `0${decimals ? '.' : ''}${Array(decimals).fill(0).join('')} ${units[0]}`
            const formatter = new Intl.NumberFormat("en-GB", {
                minimumFractionDigits: decimals,
                maximumFractionDigits: decimals,
                roundingIncrement: 5
            })
            const i = Math.floor(Math.log(value) / Math.log(k))
            const f = formatter.format(value / Math.pow(k, i))
            return `${f} ${units[i]}`
        }
    }
}