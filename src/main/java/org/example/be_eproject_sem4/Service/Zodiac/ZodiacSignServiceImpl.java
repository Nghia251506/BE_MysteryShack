package org.example.be_eproject_sem4.Service.Zodiac;

import org.example.be_eproject_sem4.Dto.Zodiac.ZodiacAskRequestDto;
import org.example.be_eproject_sem4.Dto.Zodiac.ZodiacDailyRequestDto;
import org.example.be_eproject_sem4.Dto.Zodiac.ZodiacSignResponseDto;
import org.example.be_eproject_sem4.Mapper.ZodiacSignMapper;
import org.example.be_eproject_sem4.Entity.ZodiacDaily;
import org.example.be_eproject_sem4.Repository.ZodiacSignRepository;
import org.example.be_eproject_sem4.Service.AI.AiInterpretationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ZodiacSignServiceImpl implements ZodiacSignService {

    @Autowired
    private ZodiacSignRepository zodiacSignRepository;
    private AiInterpretationService aiService;

    @Override
    public List<ZodiacSignResponseDto> getAllZodiacSigns() {
        return zodiacSignRepository.findAll().stream()
                .map(ZodiacSignMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ZodiacSignResponseDto> getActiveZodiacSigns() {
        return zodiacSignRepository.findAllByActiveTrue().stream()
                .map(ZodiacSignMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ZodiacSignResponseDto getZodiacSignById(Long id) {
        ZodiacDaily zodiacSign = zodiacSignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cung hoàng đạo với ID: " + id));
        return ZodiacSignMapper.toDto(zodiacSign);
    }

    @Override
    public ZodiacSignResponseDto createZodiacSign(ZodiacDailyRequestDto requestDto) {
        // Check trùng tên
        if (zodiacSignRepository.findByNameViIgnoreCase(requestDto.getNameVi()).isPresent()) {
            throw new RuntimeException("Tên tiếng Việt đã tồn tại");
        }
        if (zodiacSignRepository.findByNameEnIgnoreCase(requestDto.getNameEn()).isPresent()) {
            throw new RuntimeException("Tên tiếng Anh đã tồn tại");
        }

        ZodiacDaily zodiacSign = new ZodiacDaily();
        zodiacSign.setNameVi(requestDto.getNameVi());
        zodiacSign.setNameEn(requestDto.getNameEn());
        zodiacSign.setStartDate(requestDto.getStartDate());
        zodiacSign.setEndDate(requestDto.getEndDate());
        zodiacSign.setDescription(requestDto.getDescription());
        zodiacSign.setImageUrl(requestDto.getImageUrl());
        zodiacSign.setActive(true);

        zodiacSign = zodiacSignRepository.save(zodiacSign);
        return ZodiacSignMapper.toDto(zodiacSign);
    }

    @Override
    public ZodiacSignResponseDto updateZodiacSign(Long id, ZodiacDailyRequestDto requestDto) {
        ZodiacDaily zodiacSign = zodiacSignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cung hoàng đạo với ID: " + id));

        // Check trùng tên (trừ chính nó)
        if (zodiacSignRepository.existsByNameViIgnoreCaseAndIdNot(requestDto.getNameVi(), id)) {
            throw new RuntimeException("Tên tiếng Việt đã tồn tại");
        }
        if (zodiacSignRepository.existsByNameEnIgnoreCaseAndIdNot(requestDto.getNameEn(), id)) {
            throw new RuntimeException("Tên tiếng Anh đã tồn tại");
        }

        zodiacSign.setNameVi(requestDto.getNameVi());
        zodiacSign.setNameEn(requestDto.getNameEn());
        zodiacSign.setStartDate(requestDto.getStartDate());
        zodiacSign.setEndDate(requestDto.getEndDate());
        zodiacSign.setDescription(requestDto.getDescription());
        zodiacSign.setImageUrl(requestDto.getImageUrl());

        zodiacSign = zodiacSignRepository.save(zodiacSign);
        return ZodiacSignMapper.toDto(zodiacSign);
    }

    @Override
    public void deleteZodiacSign(Long id) {
        ZodiacDaily zodiacSign = zodiacSignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cung hoàng đạo với ID: " + id));
        zodiacSign.setActive(false);
        zodiacSignRepository.save(zodiacSign);
    }

    @Override
    public ZodiacSignResponseDto getZodiacSignByBirthDate(LocalDate birthDate) {
        return zodiacSignRepository.findByBirthDate(birthDate)
                .map(ZodiacSignMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cung hoàng đạo cho ngày sinh: " + birthDate));
    }

    @Override
    public ZodiacSignResponseDto getZodiacSignByNameVi(String nameVi) {
        return zodiacSignRepository.findByNameViIgnoreCase(nameVi)
                .map(ZodiacSignMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cung với tên: " + nameVi));
    }

    @Override
    public ZodiacSignResponseDto getZodiacSignByNameEn(String nameEn) {
        return zodiacSignRepository.findByNameEnIgnoreCase(nameEn)
                .map(ZodiacSignMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cung với tên tiếng Anh: " + nameEn));
    }
    @Override
    public String getZodiacAIResponse(ZodiacAskRequestDto requestDto) {
        // Tính cung hoàng đạo từ ngày sinh
        ZodiacDaily zodiac = zodiacSignRepository.findByBirthDate(requestDto.getBirthday())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cung hoàng đạo cho ngày sinh: " + requestDto.getBirthday()));

        // Gọi AiService generate response cho Zodiac (method mới đã thêm)
        return aiService.generateZodiacResponse(
                requestDto.getName(),
                requestDto.getGender(),
                requestDto.getBirthday(),
                zodiac.getNameVi()
        );
    }
}
