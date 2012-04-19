import com.k_int.kbplus.*

class BootStrap {

    def init = { servletContext ->

      def so_filetype = DataloadFileType.findByName('Subscription Offered File') ?: new DataloadFileType(name:'Subscription Offered File');
      def plat_filetype = DataloadFileType.findByName('Platforms File') ?: new DataloadFileType(name:'Platforms File');
    }
    def destroy = {
    }
}
